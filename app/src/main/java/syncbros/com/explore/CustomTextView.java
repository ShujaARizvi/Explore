package syncbros.com.explore;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class CustomTextView extends  android.support.v7.widget.AppCompatTextView {

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(),"fonts/Poppins-Regular.ttf"));
    }
}
