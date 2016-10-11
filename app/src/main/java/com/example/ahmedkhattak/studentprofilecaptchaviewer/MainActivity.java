package com.example.ahmedkhattak.studentprofilecaptchaviewer;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class MainActivity extends AppCompatActivity {

    Button btn1, btn2;
    TextView txt1, txt2;
    ImageView imageView;
    String currentURL;
    boolean isloaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn1 = (Button) findViewById(R.id.refreshpage);
        btn2 = (Button) findViewById(R.id.refreshcaptcha);
        txt1 = (TextView) findViewById(R.id.urlshow);
        txt2 = (TextView) findViewById(R.id.timershow);
        imageView = (ImageView) findViewById(R.id.captcha);

        //initial page load after this u can reload captcha multiple times with no need to reload whole page bitch suck ma dick
        //getPage();


        //reload page
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPage();

            }
        });


        //reload captcha without sending any page data
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isloaded) {
                    currentURL = currentURL.substring(0, currentURL.lastIndexOf("=") + 1);
                    currentURL = currentURL + getDateAndTime();
                    txt2.setText(currentURL);
                    getCaptchaImage();
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Pele load kar shit ko lazmi hai no shit !", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    public void getPage() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLS.endpoint,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //get captcha text
                        Toast.makeText(MainActivity.this,
                                makeFinalUrl(urlEncodeComponent(getCaptchaText(response)), getDateAndTime()), Toast.LENGTH_LONG).show();
                        getCaptchaImage();
                        isloaded = true;
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                isloaded = false;
            }
        });
        VolleySingleton.getInstance().addToRequestQueue(stringRequest);

    }


    //performs the same action as refresh button in web page only refreshing the "t" param in the url
    // with new time values
    public void getCaptchaImage() {
        ImageRequest request = new ImageRequest(currentURL,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        imageView.setImageBitmap(bitmap);
                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        imageView.setImageResource(R.color.colorAccent);
                    }
                });
        VolleySingleton.getInstance().addToRequestQueue(request);
    }

    //returns encoded url this is not encryption fyi it works the same as js urlencodecomponent so i named it the same
    public String urlEncodeComponent(String captchaURL) {
        return Uri.encode(captchaURL);
    }


    //get date and time since epoch in milliseconds verified correct since it looks same as js time !
    public String getDateAndTime() {
        return String.valueOf(System.currentTimeMillis());
    }

    public String getCaptchaText(String response) {
        //get DOM
        Document doc = Jsoup.parse(response);
        //get CaptchaText from DOM
        Elements element = doc.select("div.AspireCaptcha");
        Log.d("TEST", element.attr("Captcha"));
        return element.attr("Captcha");
    }


    public String makeFinalUrl(String encodedURl, String timeSinceEpoch) {
        currentURL = URLS.subpoint + "/HttpHandlers/Captcha.ashx?Captcha=" + encodedURl + "&t=" + timeSinceEpoch;
        txt1.setText(currentURL);
        return currentURL;
    }

}
