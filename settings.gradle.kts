dependencyResolutionManagement {
	versionCatalogs {
		create("libs") {
			from(files("configs/versions.toml"))
		}
	}
}

rootProject.name = "jcr-hopper"
