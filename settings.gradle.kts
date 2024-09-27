dependencyResolutionManagement {
	versionCatalogs {
		create("libs") {
			from(files("versions.toml"))
		}
	}
}

rootProject.name = "jcr-hopper"
