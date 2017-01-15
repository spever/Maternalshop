package subzero.ereza;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.subzero.maternalshop.R;
public class ErrorActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ereza_error_custom_activity);
		initView();
	}
	private void initView()
	{
		((TextView)findViewById(R.id.tv_error_details)).setText(CustomActivityOnCrash.getStackTraceFromIntent(getIntent()));
		Class<? extends Activity> restartActivityClass = CustomActivityOnCrash.getRestartActivityClassFromIntent(getIntent());
		if (restartActivityClass != null) {
			Intent intent = new Intent(ErrorActivity.this, restartActivityClass);
			CustomActivityOnCrash.restartApplicationWithIntent(ErrorActivity.this, intent);
		}
		else {
			CustomActivityOnCrash.closeApplication(ErrorActivity.this);
		}
	}
	
}
