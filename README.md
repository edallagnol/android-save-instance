# android-save-instance

### Usage:

Base Activity (or fragment):
```java
import com.edallagnol.androidsaveinstance.SaveInstance;

public class BaseActivity extends AppCompatActivity {
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SaveInstance.restore(this, savedInstanceState, BaseActivity.class);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		SaveInstance.save(this, outState, BaseActivity.class);
	}
}
```

Activity (or fragment):

```java
import com.edallagnol.androidsaveinstance.Save;

public class MyActivity extends BaseActivity {
	@Save private ArrayList<MyParcelable> mList;
	@Save private int mInt;
}
```

### Gradle:

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
