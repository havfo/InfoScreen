package net.fosstveit.infoscreen.fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import net.fosstveit.infoscreen.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * Created by havfo on 02.05.14.
 */
public class InfoScreenFragment extends Fragment {

    private TextView userDetails;

    private TextView calendarText;

    private TextView phoneText;

    private ConnectionHandler ch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setProgressBarIndeterminateVisibility(
                Boolean.TRUE);

        RelativeLayout rl = (RelativeLayout) inflater.inflate(
                R.layout.infoscreenfragment_layout, container, false);

        userDetails = (TextView) rl.findViewById(R.id.infoscreen_user_details);
        calendarText = (TextView) rl.findViewById(R.id.infoscreen_user_calendar);
        phoneText = (TextView) rl.findViewById(R.id.infoscreen_user_phone);

        return rl;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getUpdates();
    }

    @Override
    public void onPause() {
        super.onPause();

        ch.cancel(true);
    }

    private void getUpdates() {
        ch = new ConnectionHandler();
        ch.execute();
    }

    private class ConnectionHandler extends AsyncTask<Void, String, Void> {

        private String serverip = "127.0.0.1";
        private int serverport = 9999;
        private String message;

        private Socket s = null;
        private BufferedReader br;
        private BufferedWriter bw;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Log.i("AsyncTank", "doInBackgoung: Creating Socket");
                s = new Socket(serverip, serverport);

                if (s.isConnected()) {
                    try {
                        br = new BufferedReader(new InputStreamReader(
                                s.getInputStream()));
                        bw = new BufferedWriter(new OutputStreamWriter(
                                s.getOutputStream()));
                        Log.i("AsyncTank",
                                "doInBackgoung: Socket created, Streams assigned");

                    } catch (Exception e) {
                        Log.i("AsyncTank",
                                "doInBackgoung: Cannot assign Streams, Socket not connected");
                        e.printStackTrace();
                    }
                } else {
                    Log.i("AsyncTank",
                            "doInBackgoung: Cannot assign Streams, Socket is closed");
                }

                String line = null;
                while (!(isCancelled()) && (line = readFromStream()) != null) {
                    handleLine(line);
                }

            } catch (Exception e) {
                Log.i("AsyncTank", "doInBackgoung: Cannot create Socket");
            } finally {
                try {
                    br.close();
                    bw.close();
                    s.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onCancelled() {
            Log.i("AsyncTask", "Cancelled.");
        }

        @Override
        protected void onProgressUpdate(String... params) {
            String command = params[0];
            String content = params[1];

            if (command.equals("phone")) {
                userDetails.setText(content);
            } else if (command.equals("bluetooth")) {
                userDetails.setText(content);
            } else if (command.equals("calendar")) {
                content = content.replaceAll("<n>", "\n");
                calendarText.setText(content);
            } else if (command.equals("presence")) {
                int severity = Integer.parseInt(content);

                switch (severity) {
                    case 0:
                        getView().setBackgroundColor(Color.GRAY);
                        break;
                    case 1:
                        getView().setBackgroundColor(Color.GREEN);
                        break;
                    case 2:
                        getView().setBackgroundColor(Color.RED);
                        break;
                    default:
                        break;
                }
            }
        }

        private void handleLine(String line) {
            String[] command = line.split("<split>");

            if (command.length == 2) {
                command(command[0], command[1]);
            }
        }

        private void command(String command, String content) {
            publishProgress(command, content);
        }

        private String readFromStream() {
            try {
                if (s != null && s.isConnected()) {
                    Log.i("AsynkTask", "readFromStream : Reading message");
                    message = br.readLine();
                    Log.i("AsynkTask", "Message: " + message);
                } else {
                    Log.i("AsynkTask",
                            "readFromStream : Cannot Read, Socket is closed");
                }
            } catch (Exception e) {
                Log.i("AsynkTask", "readFromStream : Writing failed");
            }
            return message;
        }
    }
}
