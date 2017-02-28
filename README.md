# android-save-instance

//#TODO

Maven Repository: https://mymavenrepo.com/repo/Ghd1bN1WIPA0LBBLKxW8/

###Gradle

Project build.gradle:

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
	compile group: 'com.edallagnol', name: 'android-save-instance', version: '0.3'
}
</pre>
