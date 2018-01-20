package lateralpraxis.lpsmartfarm;

import android.app.Activity;
import android.os.Bundle;

public class ActivityClose extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        finish(); // Exit
    }
}
