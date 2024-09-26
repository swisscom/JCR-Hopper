dependencyResolutionManagement {
	versionCatalogs {
		create("libs") {
			from(files("versions.toml"))
		}
	}
}

rootProject.name = "JCR Hopper"
