package ua.yman07.pyon;

import android.app.*;
import android.os.*;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEventListener;
import android.hardware.SensorEvent;
import android.widget.*;
import android.view.*;
import android.view.SurfaceHolder.*;
import android.content.pm.*;
import android.graphics.*;
import java.io.*;
import android.util.Log;
import android.widget.SeekBar.*;
import android.content.*;

public class MainActivity extends Activity implements SensorEventListener, SurfaceHolder.Callback, Camera.AutoFocusCallback, SeekBar.OnSeekBarChangeListener
{
	private SensorManager manager;
	private Sensor sensor;
	private static Camera camera;
	private SurfaceHolder holder;
	private SurfaceView surface;
	private TextView t;
	private Paint p;
	private Paint p2;
	private Paint p3;
	private SeekBar seek;
	private Bitmap plamb;
	private float calibr = 3.5f;
	private int calibr2 = 0;
	private float cur = 0f;
	private int cur2 = 0;
	private String CALIBR = "CALIBR";
	private String CALIBR2 = "CALIBR2";
	private String BLACK = "BLACK";
	private SharedPreferences pref;
	
	public SurfaceView surface2;
	public SurfaceHolder holder2;
	private boolean created = false;
	private boolean released = false;
	private CheckBox black;
	
	private Path pathD;// = new Path();
	private Path pathP;// = new Path();
	//private Matrix matrix = new Matrix();
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT);
		t = (TextView) findViewById(R.id.text);
		
		pathD = new Path();
		pathP = new Path();
		
		surface = (SurfaceView) findViewById(R.id.surfaceView);
		surface2 = (SurfaceView) findViewById(R.id.surfaceView2);
		holder2 = surface2.getHolder();
		holder2.setFormat(PixelFormat.TRANSPARENT);
		surface2.setBackgroundColor(Color.TRANSPARENT);
		surface2.setZOrderMediaOverlay(true);
		holder =  surface.getHolder();
		holder.addCallback(this);
		//holder2.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		manager = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensor = manager.getDefaultSensor(Sensor.TYPE_GRAVITY);
		p = new Paint(Paint.ANTI_ALIAS_FLAG);
		p2 = new Paint(Paint.ANTI_ALIAS_FLAG);
		p.setStyle(Paint.Style.STROKE);
		p2.setStyle(Paint.Style.STROKE);
		p.setColor(Color.RED);
		p2.setARGB(85,255,255,255);
		p.setStrokeWidth(1.0f);
		p2.setStrokeWidth(3.0f);
		p3 = new Paint(Paint.ANTI_ALIAS_FLAG);
		p3.setStyle(Paint.Style.STROKE);
		p3.setStrokeWidth(3.0f);
		p3.setARGB(85,0,255,0);
		//manager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
		seek = (SeekBar) findViewById(R.id.seekBar);
		seek.setOnSeekBarChangeListener(this);
		Bitmap t_p = BitmapFactory.decodeResource(getResources(), R.drawable.plumb);
		plamb = Bitmap.createScaledBitmap(t_p, 50, 200, false);
		pref = getSharedPreferences(CALIBR, Activity.MODE_PRIVATE);
		calibr = pref.getFloat(CALIBR, 0f);
		calibr2 = pref.getInt(CALIBR2, 0);
		if (savedInstanceState != null){
			calibr = savedInstanceState.getFloat(CALIBR, 0f);
			calibr2 = savedInstanceState.getInt(CALIBR2, 0);
			black.setChecked(savedInstanceState.getBoolean(BLACK, false));
		}
		black = (CheckBox) findViewById(R.id.mainCheckBox1);
		
    }

	@Override
	public void surfaceCreated(SurfaceHolder p1)
	{
		// TODO: Implement this method
		try {
			if (released) {
				camera.setPreviewDisplay(holder);
				camera.startPreview();
			}
			created = true;
		} catch (IOException e) {
			Log.e("CAMERA", e.toString());
			}

		int w = surface.getWidth();
		int h = surface.getHeight();
		//Log.i("PYON", "W "+w+" H "+h);
		pathD.reset();
		pathP.reset();
		pathD.moveTo(w/5*1, 0f);
		pathD.lineTo(w/5*1, h);
		pathD.moveTo(w/5*2, 0f);
		pathD.lineTo(w/5*2, h);
		pathD.moveTo(w/5*3, 0f);
		pathD.lineTo(w/5*3, h);
		pathD.moveTo(w/5*4, 0f);
		pathD.lineTo(w/5*4, h);
		pathD.close();
		pathP.moveTo(w/2, 0);
		pathP.lineTo(w/2, h-200);
	}

	@Override
	public void surfaceChanged(SurfaceHolder p1, int p2, int w, int h)
	{
		try {
			camera.reconnect();
		} catch (IOException e) {}
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder p1)
	{
		// TODO: Implement this method
		if (released) {
			camera.stopPreview();
			camera.release();
		}
		created = false;
	}
	
	/*private float degree(float[] v) {
		return (float) Math.toDegrees(Math.acos(v[1]/Math.sqrt(Math.pow(9.807, 2) - Math.pow(Math.abs(v[2]), 2))));
		//return (float) Math.toDegrees(Math.acos(x/9.807));
	}
	
	private float degree2(float x) {
		return (float) Math.toDegrees(Math.asin(x/9.807));
	}*/
	
	private float degree3(float[] v){
		//return (float) Math.toDegrees(Math.asin(v[0]/Math.sqrt(Math.pow(9.807, 2) - Math.pow(Math.abs(v[2]), 2))));
		return (float) Math.toDegrees(Math.atan(v[0]/Math.abs( v[1])));
	}
	
	private int div(float[] v){
		//float z = (float) Math.sqrt(Math.pow(9.807, 2) - Math.pow(v[2], 2));
		//float d = (float) Math.sqrt(Math.pow(z, 2) + Math.pow(v[1], 2));
		//return (int) (v[0]/Math.sqrt(Math.pow(9.807, 2) - Math.pow(v[2], 2))*1000);
		return (int) (v[0]/Math.abs( v[1])*1000);
	}

	@Override
	public void onSensorChanged(SensorEvent p1)
	{
		// TODO: Implement this method
		//String s = String.format("x = %1$2.1f°, y = %2$2.1f°, z = %3$2.1f° [%4$d]", degree3(p1.values), degree(p1.values), degree2(p1.values[2]), camera.getParameters().getMaxZoom());
		if (created && released) {
			created = false;
			String s = String.format("%1$2.1f°%n%2$3dmm", -(degree3(p1.values)+calibr), Math.abs(div(p1.values)+calibr2));
			t.setText(s);
			/*if (black.isChecked()){
				t.setTextColor(Color.BLACK);
			} else {
				t.setTextColor(Color.WHITE);
			}*/
			Canvas c = holder2.lockCanvas();
			c.drawColor(0, PorterDuff.Mode.CLEAR);
			cur = degree3(p1.values);
			cur2 = div(p1.values);
			float cur1 = cur + calibr;
			/*if (-0.3f < cur1 && cur1 < 0.3f) {
				//p2.setColor(Color.GREEN);
				if (black.isChecked()){
					p2.setARGB(85,0,127,0);
				} else {
					p2.setARGB(85,0,255,0);
				}
			} else {
				//p2.setColor(Color.WHITE);
				if (black.isChecked()){
					p2.setARGB(85,0,0,0);
				} else {
					p2.setARGB(85,255,255,255);
				}
			}*/
			
			c.rotate(cur1, surface2.getWidth()/2, 0);// surface2.getHeight()/2);
			//c.drawLine(surface2.getWidth()/2, 0, surface2.getWidth()/2, surface2.getHeight()-200, p);
			c.drawPath(pathP, p);
			c.drawBitmap(plamb, surface2.getWidth()/2-25, surface2.getHeight()-200, p);
			c.rotate(-cur1, surface2.getWidth()/2, 0);// surface2.getHeight()/2);

			if (-0.3 < cur1 && cur1 < 0.3){
				c.drawPath(pathD, p3);
			} else {
				c.drawPath(pathD, p2);
			}
			//c.drawPath(pathD, p2);
			/*
			c.drawLine(surface2.getWidth()/5*1, 0, surface2.getWidth()/5*1, surface2.getHeight(), p2);
			c.drawLine(surface2.getWidth()/5*2, 0, surface2.getWidth()/5*2, surface2.getHeight(), p2);
			c.drawLine(surface2.getWidth()/5*3, 0, surface2.getWidth()/5*3, surface2.getHeight(), p2);
			c.drawLine(surface2.getWidth()/5*4, 0, surface2.getWidth()/5*4, surface2.getHeight(), p2);
			*/
			holder2.unlockCanvasAndPost(c);
			created = true;
		}
	}

	@Override
	protected void onResume()
	{
		// TODO: Implement this method
		super.onResume();
		manager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
		camera = Camera.open();
		camera.setDisplayOrientation(90);
		Camera.Parameters p = camera.getParameters();
		p.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
		seek.setMax(p.getMaxZoom());
		seek.setProgress(p.getZoom());
		camera.setParameters(p);
		released = true;
	}

	@Override
	public void onAccuracyChanged(Sensor p1, int p2)
	{
		// TODO: Implement this method
	}

	@Override
	protected void onPause()
	{
		// TODO: Implement this method
		manager.unregisterListener(this, sensor);
		camera.stopPreview();
		camera.release();
		released = false;
		super.onPause();
	}

	@Override
	protected void onStop()
	{
		// TODO: Implement this method
		if (released){
			camera.stopPreview();
			camera.release();
			released = false;
		}
		super.onStop();
	}
	

	@Override
	public void onAutoFocus(boolean p1, Camera p2)
	{
		// TODO: Implement this method
	}
	
	public void focus(View v) {
		camera.autoFocus(this);
	}
	
	public void calibrate(View v){
		calibr = -cur;
		calibr2 = -cur2;
		SharedPreferences.Editor editor = pref.edit();
		editor.putFloat(CALIBR, calibr);
		editor.putInt(CALIBR2, calibr2);
		editor.apply();
	}
	
	public void blackClick(View v){
		if (black.isChecked()){
			p2.setARGB(85,0,0,0);
			p3.setARGB(85,0,127,0);
			t.setTextColor(Color.BLACK);
		} else {
			p2.setARGB(85,255,255,255);
			p3.setARGB(85,0,255,0);
			t.setTextColor(Color.WHITE);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		// TODO: Implement this method
		outState.putFloat(CALIBR, calibr);
		outState.putInt(CALIBR2, calibr2);
		outState.putBoolean(BLACK, black.isChecked());
		super.onSaveInstanceState(outState);
	}
	
	

	@Override
	protected void onDestroy()
	{
		// TODO: Implement this method
		super.onDestroy();
		if (camera != null) {
			camera.stopPreview();
			camera.release();
		}
	}

	@Override
	public void onProgressChanged(SeekBar p1, int p2, boolean p3)
	{
		// TODO: Implement this method
		if (camera != null) {
			Camera.Parameters p = camera.getParameters();
			p.setZoom(seek.getProgress());
			camera.setParameters(p);
		}
	}
	
	@Override
	public void onStartTrackingTouch(SeekBar p1)
	{
		// TODO: Implement this method
	}

	@Override
	public void onStopTrackingTouch(SeekBar p1)
	{
		// TODO: Implement this method
		
		if (camera != null) {
			camera.autoFocus(this);
		}
	}
}
