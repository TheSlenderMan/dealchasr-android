package uk.co.almanacmedia.dealchasr.dealchasr;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import uk.co.almanacmedia.dealchasr.dealchasr.R;

public class ViewVoucherActivity extends AppCompatActivity {

    private Integer userID;
    private Integer voucherID;
    private Integer venueID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_voucher);

        userID = getIntent().getIntExtra("userID", 0);
        voucherID = getIntent().getIntExtra("voucherID", 0);
        venueID = getIntent().getIntExtra("venueID", 0);

        TextView voucherVID = findViewById(R.id.voucherVID);
        voucherVID.setText("VOUCHER ID: " + voucherID);

        new DoGetVoucher(ViewVoucherActivity.this, userID, voucherID, findViewById(android.R.id.content)).execute();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ViewVoucherActivity.this, MainMapActivity.class);
        intent.putExtra("VID", (Integer) venueID);
        ViewVoucherActivity.this.startActivity(intent);
        ((Activity)ViewVoucherActivity.this).finish();
    }
}
