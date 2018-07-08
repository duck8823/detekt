import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.regex.Pattern
import java.util.stream.Collectors

if (args.size() != 3) throw new IllegalArgumentException("Usage: [directory] [search] [replacement]")

def dir = Paths.get(args[0])

if (Files.notExists(dir) || !Files.isDirectory(dir)) {
	throw new IllegalArgumentException("need an existing directory to work.")
}

def search = args[1]
def replacement = args[2]

if (search.isEmpty() || replacement.isEmpty()) {
	throw new IllegalArgumentException("search and replacement must not be empty")
}

Pattern pattern = Pattern.compile(".*/formatting/wrappers/.*")
def LINE_SEP = System.getProperty("line.separator")

Files.walk(dir)
		.filter { Files.isRegularFile(it) }
		.filter { it.toString().endsWith(".kt") }
		.filter { pattern.matcher(it.toString()).matches() }
		.forEach { replace_search_pattern(it, search, replacement, LINE_SEP) }

static def replace_search_pattern(Path path, String search, String replacement, String sep) {
	def content = Files.readAllLines(path)
			.stream()
			.map {
		if (it.trim().startsWith("*") && it.contains(search)) {
			it.replace(search, replacement)
		} else {
			it
		}
	}.collect(Collectors.joining(sep)) + sep
	Files.write(path, content.getBytes())
}

// following code when copied inside:
// if (it.trim....) { ... } else { it } can parse KtLint's website suffixes

//			def index = it.indexOf(search) + search.length()
//			def ref = it.substring(index, index + 2)
//			if (ref == "/#") {
//				def afterLink = it.substring(index)
//				def spaceAfterNavigation = afterLink.indexOf(' ') + 1
//				def linkSuffix = afterLink.substring(0, spaceAfterNavigation - 1)
//				def searchInReplacementIndex = replacement.indexOf(search)
//				def from = searchInReplacementIndex + search.length()
//				def newReplacement = replacement.substring(0, from) + linkSuffix + replacement.substring(from)
//				it.replace(search + linkSuffix, newReplacement)
//			} else {
//				it.replace(search, replacement)
//			}
