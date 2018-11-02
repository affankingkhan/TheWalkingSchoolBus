package ca.cmpt276.walkinggroup.app;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import ca.cmpt276.walkinggroup.dataobjects.Theme;

public class MonitoringAndGroupsActivity extends AppCompatActivity {

    private static final String EXTRA_IS_GROUPS = "ca.cmpt276.walkinggroup.app, MonitoringAndGroupsActivity  - is User I Groups?";

    boolean isGroupsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Theme.setColourScheme(this);

        extractDataFromIntent();

    }

    public static Intent makeMonitoringAndGroupIntent(Context context, boolean isGroups) {
        Intent intent = new Intent(context, MonitoringAndGroupsActivity.class);
        intent.putExtra(EXTRA_IS_GROUPS, isGroups);
        return intent;
    }

    private void extractDataFromIntent() {

        Intent intent = getIntent();
        isGroupsButton = intent.getBooleanExtra(EXTRA_IS_GROUPS, false);
        setLayout();
    }

    private void setLayout() {
        if (isGroupsButton) {
            setContentView(R.layout.activity_group_menu);
            setupGroupLeaderButton();
            setupGroupsIAmInButton();
        }
        else{
            setContentView(R.layout.activity_monitoring_menu);
            setupMonitoringOthersButton();
            setupMonitoringMeButton();
        }
    }

    private void setupMonitoringOthersButton() {
        Button monitoring = (Button) findViewById(R.id.btnMonitoring);
        monitoring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent monitoringOthersIntent = MonitoringActivity.makeMonitoringIntent(MonitoringAndGroupsActivity.this, true);
                startActivity(monitoringOthersIntent);

            }
        });
    }

    private void setupMonitoringMeButton() {
        Button btnMonitors = findViewById(R.id.btnMonitorMe);
        btnMonitors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent monitorMeIntent = MonitoringActivity.makeMonitoringIntent(MonitoringAndGroupsActivity.this, false);
                startActivity(monitorMeIntent);

            }
        });
    }

    private void setupGroupLeaderButton() {
        Button groupleader = (Button) findViewById(R.id.btnGroupLead);
        groupleader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent leadergroup = GroupsILeadActivity.makeGroupsILeadIntent(MonitoringAndGroupsActivity.this);
                startActivity(leadergroup);
            }
        });
    }

    private void setupGroupsIAmInButton() {
        Button btn = (Button)findViewById(R.id.btnGroups);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = GroupsIAmInActivity.makeGroupsIAmInIntent(MonitoringAndGroupsActivity.this);
                startActivity(intent);
            }
        });
    }
}
