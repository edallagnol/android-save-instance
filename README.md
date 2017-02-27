# android-save-instance

//#TODO

Maven Repository: https://mymavenrepo.com/repo/Ghd1bN1WIPA0LBBLKxW8/

###Gradle

Top build.gradle:

<pre>
allprojects {
    repositories {
        ...
        // com.edallagnol
        maven { url "https://mymavenrepo.com/repo/Ghd1bN1WIPA0LBBLKxW8/" }
    }
}
</pre>

Module build.gradle:

<pre>
dependencies {
	...
	compile group: 'com.edallagnol', name: 'save-instance-state', version: '0.1'
}
</pre>
