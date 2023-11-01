package com.example.nfcsniffer;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.Trace;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.widget.Toast.LENGTH_SHORT;

public class NfcSniffer extends Activity {
    NfcAdapter mNfcAdapter;
    readerCallback mReaderCallback = new readerCallback();
    private PendingIntent mPendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Log.d("NfcSniffer", "onCreate " + intent);

        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        //NfcAdapter.ReaderCallback readerCallback;

        //mNfcAdapter.enableReaderMode(this, mReaderCallback, NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS, savedInstanceState);

        if ( intent.getAction() == Intent.ACTION_MAIN)
            ;
        else if ( intent.getAction() == NfcAdapter.ACTION_TECH_DISCOVERED)
        {
            HandleIntent(intent);
            this.finish();
            return;
        }

        try {
            Object m = Class.forName("android.os.Trace").getMethods();
            Method m3 = Class.forName("android.os.Trace").getMethod("isEnabled", new Class[] {});
            Method m2 = Class.forName("android.os.Trace").getMethod("setAppTracingAllowed", new Class[] { boolean.class});
            Object o = m2.invoke(null, true);

            Method traceBegin = android.os.Trace.class.getMethod("traceBegin", new Class[] { long.class, java.lang.String.class});
            Object tbo = traceBegin.invoke(null,new Object[] { 1L << 12 /*TRACE_TAG_APP*/, "bob" });
            boolean b = Trace.isEnabled();

            Log.d ( "M", m.toString());
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    protected class readerCallback implements NfcAdapter.ReaderCallback
    {
        @Override
        public void onTagDiscovered(Tag tag)
        {
            Log.d("NfcSniffer", "onTagDiscovered " + tag.toString());
            interrogateTag ( tag );
            //mNfcAdapter.enableReaderMode(this, mReaderCallback, NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS, null);

        }

    }
    @Override
    protected void onStart () {
        super.onStart();
        Log.d("NfcSniffer", "onStart");
    }
    @Override
    protected void onResume () {
        super.onResume();
        Log.d("NfcSniffer", "onResume");

        if (mNfcAdapter != null) {
            mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
            //mNfcAdapter.enableReaderMode(this, mReaderCallback, NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS, null);
        }
    }
    protected void onPause() {
        super.onPause();
        Log.d("NfcSniffer", "onPause");
        if (mNfcAdapter != null) {
            mNfcAdapter.disableForegroundDispatch(this);
            //mNfcAdapter.disableReaderMode(this);
        }
    }
    @Override
    public void onNewIntent(Intent intent) {
        Log.d("NfcSniffer", "onNewIntent");
        super.onNewIntent(intent);
        HandleIntent(intent);
    }
    void HandleIntent (Intent intent)
    {
        Log.i("NfcSniffer", "Discovered tag with intent: " + intent);

        Parcelable rawTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        Parcelable[] rawId = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_ID);

        Log.i("NfcSniffer", "EXTRA_TAG=" + rawTag.toString() + "\n");

        if (rawId != null )
            Log.i ("NfcSniffer", "EXTRA_ID= " + rawId.toString());

        interrogateTag( (Tag)rawTag);

    }

    void interrogateTag ( Tag tag ) {
        Log.i ("NfcSniffer", "Tag.getID(): " + byteArrayToString(tag.getId()));

        NfcA nfcA = NfcA.get(tag);
        if (nfcA != null ) {
            Log.i ("NfcSniffer", "nNfcA: " + nfcA.toString());
            Log.i ("NfcSniffer" ,"NfcA.getAtqa() = " + byteArrayToString(nfcA.getAtqa()));
            Log.i ("NfcSniffer","NfcA.getSak() = " + nfcA.getSak());
            Log.i ("NfcSniffer" ,"NfcA.getMaxTransceiveLength() = " + nfcA.getMaxTransceiveLength());
        }
        NfcB nfcB = NfcB.get(tag);
        if (nfcB != null ) {
            Log.i ("NfcSniffer","NfcB: " + nfcB.toString());
            Log.i ("NfcSniffer","NfcB.getApplicationData() = " + byteArrayToString(nfcB.getApplicationData()));
            Log.i ("NfcSniffer","NfcB.getProtocolInfo() = " + byteArrayToString(nfcB.getProtocolInfo()));
            Log.i ("NfcSniffer","NfcB.getMaxTransceiveLength() = " + nfcB.getMaxTransceiveLength());
        }
        NfcF nfcF = NfcF.get(tag);
        if (nfcF != null ) {
            Log.i ("NfcSniffer", "NfcF: " + nfcF.toString());
        }
        NfcV nfcV = NfcV.get(tag);
        if (nfcV != null ) {
            Log.i ("NfcSniffer", "NfcV: " + nfcV.toString());
        }

        IsoDep isoDep = IsoDep.get(tag);
        if (isoDep != null ) {
            Log.i ("NfcSniffer","IsoDep: " + isoDep.toString());
            Log.i ("NfcSniffer","IsoDep.getHiLayerResponse() = " + byteArrayToString(isoDep.getHiLayerResponse()));
            Log.i ("NfcSniffer","IsoDep.getHistoricalBytes() = " + byteArrayToString(isoDep.getHistoricalBytes()));
            Log.i ("NfcSniffer", "IsoDep.getMaxTransceiveLength() = " + isoDep.getMaxTransceiveLength());

            try {
                isoDep.connect();
                //byte[]response = isoDep.transceive(new byte[]{0x00, (byte)0xa4, 0x00, 0x00, 0x00, 0x00 });
                // Try and select Entry Point app, which lists all payment apps
                byte[] responseEntryPoint = isoDep.transceive(new byte[] {0x00, (byte)0xa4, 0x04, 0x00, 0x0E ,(byte)'2', 'P', 'A', 'Y','.','S','Y','S','.','D','D','F','0','1',0 });
                Log.i ("NfcSniffer" ,"IsoDep.transceive(SELECT '2PAY.SYS.DDF01') = " + byteArrayToString(responseEntryPoint));

                Pattern mastPattern = Pattern.compile("\\x4f\\x07\\xa0\\x00\\x00\\x00\\x04\\x10\\x10"); //, Pattern.MULTILINE | Pattern.UNICODE_CASE | Pattern.LITERAL);
                Pattern visaPattern = Pattern.compile("\\x4f\\x07\\xa0\\x00\\x00\\x00\\x03\\x10\\x10"); //, Pattern.MULTILINE | Pattern.UNICODE_CASE | Pattern.LITERAL);

                if (mastPattern.matcher(new String(responseEntryPoint, "ISO-8859-1")).find())
                {
                    byte[] response = isoDep.transceive(new byte[] {0x00, (byte)0xa4, 0x04, 0x00, 0x07 ,(byte)0xA0, 0x00, 0x00,0x00,0x04, 0x10, 0x10, 0 });
                    Log.i ("NfcSniffer", "IsoDep.transceive(SELECT) = " + byteArrayToString(response));

                    byte[] response4 = isoDep.transceive(new byte[]{(byte) 0x00, (byte) 0xB2, (byte) 0x01, (byte) 0x0C, 0});
                    Log.i ("NfcSniffer", "IsoDep.transceive(Read Record 1) = " + byteArrayToString(response4));

                    Pattern p2 = Pattern.compile("B(\\d{14,20})\\^"); //, Pattern.MULTILINE | Pattern.UNICODE_CASE | Pattern.LITERAL);
                    Matcher m2 = p2.matcher(new String(response4));
                    if (m2.find()) {
                        Log.i("NfcSniffer", "CardNumber = " + m2.group(1));
                        Toast.makeText(this, "Card Read: PAN=" + m2.group(1), LENGTH_SHORT).show();
                    }
                }
                // Check if Visa card
                else if (  visaPattern.matcher(new String(responseEntryPoint,"ISO-8859-1")).find())
                {
                    byte[] response = isoDep.transceive(new byte[] {0x00, (byte)0xa4, 0x04, 0x00, 0x07 ,(byte)0xA0, 0x00, 0x00,0x00,0x03, 0x10, 0x10, 0 });
                    Log.i ("NfcSniffer", "IsoDep.transcieve(SELECT) = " + byteArrayToString(response));

                    for ( int j = 1; j < 10; j++ )
                    {
                        byte[] response4 = isoDep.transceive(new byte[]{(byte) 0x00, (byte) 0xB2, (byte)j, (byte) 0x1C, 0});
                        Log.i ("NfcSniffer", "IsoDep.transceive(Read Record " + j + ", SFI=3) = " + byteArrayToString(response4));

                        Pattern PANpattern = Pattern.compile ( "5a 08 (.{24})" );
                        Matcher m2 = PANpattern.matcher(byteArrayToString(response4));
                        if ( m2.find())
                        {
                            Log.i ("NfcSniffer", "\nPAN: " + m2.group(1));
                            //Toast.makeText(this, "DavesNfcActivity", "PAN: " + m2.group(1), LENGTH_SHORT).show();
                            Toast.makeText ( this, "Card Read: PAN=" + m2.group(1), LENGTH_SHORT).show();
                            break;
                        }
                    }

                }
                isoDep.close();


            } catch (IOException e) {
                e.printStackTrace();
                Log.i ("NfcSniffer", ""+ e.toString());
            }
        }

    }

    String byteArrayToString( byte[] input )
    {
        if ( input == null )
            return "(null)";
        StringBuilder s = new StringBuilder( "(" + input.length + ") ");
        for (byte b: input ) {
            s.append ( String.format("%02x ", b));
        }
        return s.toString();
    }
}
