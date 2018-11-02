package ca.cmpt276.walkinggroup.app;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import ca.cmpt276.walkinggroup.dataobjects.Theme;

/**
 * Shows all resources in hyperlinks
 */
public class ResourcesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Theme.setColourScheme(this);
        setContentView(R.layout.activity_resources);
        
        setupLinksText();
    }

    public static Intent makeResourcesIntent(Context context) {
        return new Intent(context, ResourcesActivity.class);
    }

    private void setupLinksText() {
        TextView t2 = findViewById(R.id.txtResources);
        t2.setMovementMethod(LinkMovementMethod.getInstance());
    }


}
