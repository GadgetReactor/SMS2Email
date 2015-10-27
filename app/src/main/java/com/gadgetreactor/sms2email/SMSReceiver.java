package com.gadgetreactor.sms2email;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsMessage;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class SMSReceiver extends BroadcastReceiver {

    public static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
    private SharedPreferences settings;


    @Override
    public void onReceive(Context context, Intent intent) {

        settings = context.getSharedPreferences("Default", Context.MODE_PRIVATE);
        // TODO Auto-generated method stub
        if (intent.getAction().equals(ACTION)){
            Bundle bundle = intent.getExtras();
            if (bundle != null){
                Object[] pdus = (Object[]) bundle.get("pdus");
                SmsMessage[] messages = new SmsMessage[pdus.length];
                for (int i = 0; i < pdus.length; i++){
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }
                for (SmsMessage message : messages){

                    String strMessageFrom = message.getDisplayOriginatingAddress();
                    String strMessageBody = message.getDisplayMessageBody();

                    //Resolving the contact name from the contacts.
                    Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(strMessageFrom));
                    Cursor c = context.getContentResolver().query(lookupUri, new String[]{ContactsContract.Data.DISPLAY_NAME},null,null,null);
                    try {
                        c.moveToFirst();
                        strMessageFrom = c.getString(0);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }finally{
                        c.close();
                    }

                    Toast.makeText(context, "SMS Message received from: " +strMessageFrom, Toast.LENGTH_LONG).show();
                    Toast.makeText(context, "SMS Message content: " +strMessageBody, Toast.LENGTH_LONG).show();

                    final String fwdemail = settings.getString("fwdemail", "");
                    //TODO: Add a filter condition (if strMessageBody.toLowerCase().contains(strFilter.toLowerCase())
                    sendMail(context, fwdemail,strMessageFrom, strMessageBody);
                }
            }
        }
    }


    private void sendMail(Context context, String email, String subject, String messageBody) {
        Session session = createSessionObject(context);

        try {
            Message message = createMessage(context, email, subject, messageBody, session);
            new SendMailTask().execute(message);
        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private Message createMessage(Context ctx, String email, String subject, String messageBody, Session session) throws MessagingException, UnsupportedEncodingException {
        Message message = new MimeMessage(session);

        final String dispname = settings.getString("dispname", "");
        final String dispemail = settings.getString("dispemail", "");

        message.setFrom(new InternetAddress(dispemail, dispname));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(email, email));
        message.setSubject(subject);
        message.setText(messageBody);
        return message;
    }

    private Session createSessionObject(Context ctx) {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        final String username = settings.getString("Username", "");
        final String password = settings.getString("Password", "");

        return Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    private class SendMailTask extends AsyncTask<Message, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Message... messages) {
            try {
                Transport.send(messages[0]);
            } catch (MessagingException e) {
                e.printStackTrace();

            }
            return null;
        }
    }
}

/*
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;
import android.os.AsyncTask;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;


public class SMSReceiver extends BroadcastReceiver {

    private SharedPreferences settings;
    @Override
    public void onReceive(Context context, Intent intent) {
        //---get the SMS message passed in--
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs = null;
        String sender = "Msg from ";
        String message = "";
        if (bundle != null) {
            //---retrieve the SMS message received--
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];

            for (int i=0; i<msgs.length; i++){
                msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                if (i==0) {
                    //---get the sender address/phone number--
                    sender += msgs[i].getOriginatingAddress();
                }
                //Resolving the contact name from the contacts.
                Uri lookupUri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(sender));
                Cursor c = context.getContentResolver().query(lookupUri, new String[]{ContactsContract.Data.DISPLAY_NAME},null,null,null);
                try {
                    c.moveToFirst();
                    String  displayName = c.getString(0);
                    String ContactName = displayName;
                    sender = "Msg from " + ContactName;
                } catch (Exception e) {
                    // TODO: handle exception
                }finally{
                    c.close();
                }
                //---get the message body--
                message += msgs[i].getMessageBody().toString();
            }

            //---display the new SMS message--

            settings = context.getSharedPreferences("Default", Context.MODE_PRIVATE);
            final String fwdemail = settings.getString("fwdemail", "");

            sendMail(context, fwdemail,sender, message);
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }

    }

}
*/