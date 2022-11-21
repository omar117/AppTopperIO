package cn.encmed.websockettest;

import android.content.Context;
import android.graphics.Color;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import cn.encmed.websockettest.models.TupperIO;
import okhttp3.WebSocket;


public class ListaTupperIOAdapter extends RecyclerView.Adapter<ListaTupperIOAdapter.ViewHolder> {

    private ArrayList<TupperIO> dataset;
    private Context context;
    private WebSocket webSocket;

    public ListaTupperIOAdapter(Context context) {
        this.context = context;
        dataset = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tupper_io, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TupperIO p = dataset.get(position);
        holder.nombreTextView.setText(p.getUid());
        holder.dayTextView.setText(p.getDay());

        //Calcular la la diferencia de dias
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        // Try Class
        try {

            // parse method is used to parse
            // the text from a string to
            // produce the date
            Date d1 = sdf.parse(p.getDay()); //Fecha de arduino
            Date d2 = sdf.parse(sdf.format(Calendar.getInstance().getTime())); //Fecha actual del android

            // Calucalte time difference
            // in milliseconds
            long difference_In_Time
                    = d2.getTime() - d1.getTime();

            // Calucalte time difference in seconds,
            // minutes, hours, years, and days
            long difference_In_Seconds
                    = TimeUnit.MILLISECONDS
                    .toSeconds(difference_In_Time)
                    % 60;

            long difference_In_Minutes
                    = TimeUnit
                    .MILLISECONDS
                    .toMinutes(difference_In_Time)
                    % 60;

            long difference_In_Hours
                    = TimeUnit
                    .MILLISECONDS
                    .toHours(difference_In_Time)
                    % 24;

            long difference_In_Days
                    = TimeUnit
                    .MILLISECONDS
                    .toDays(difference_In_Time)
                    % 365;

            long difference_In_Years
                    = TimeUnit
                    .MILLISECONDS
                    .toDays(difference_In_Time)
                    / 365l;

            String duracion = "";

            if(difference_In_Years > 0){
                if(difference_In_Years == 1){
                    duracion = duracion + difference_In_Years + "Año ";
                }else{
                    duracion = duracion + difference_In_Years + "Años ";
                }
            }
            if(difference_In_Days > 0 ){
                if(difference_In_Days == 1){
                    duracion = duracion + difference_In_Days + " Dia ";
                }else{
                    duracion = duracion + difference_In_Days + " Dias ";
                }
            }
            if(difference_In_Hours > 0){
                if(difference_In_Hours == 1){
                    duracion = duracion + difference_In_Hours + " Hora ";
                }else{
                    duracion = duracion + difference_In_Hours + " Horas ";
                }
            }
            if(difference_In_Minutes > 0){
                if(difference_In_Minutes == 1){
                    duracion = duracion + difference_In_Minutes + " Minuto ";
                }else{
                    duracion = duracion + difference_In_Minutes + " Minutos ";
                }
            }
            if(difference_In_Seconds > 0){
                if(difference_In_Seconds == 1){
                    duracion = duracion + difference_In_Seconds + " Segundo ";
                }else{
                    duracion = duracion + difference_In_Seconds + " Segundos ";
                }
            }

            holder.durationDateTextView.setText(duracion);

            //Limite de caducidad 15 min.
            if(difference_In_Minutes > 10 || difference_In_Hours > 0 || difference_In_Days > 0 || difference_In_Years > 0){
                //holder.layoutBackground.setBackground(ContextCompat.getDrawable(context, R.color.colorRed));
                holder.eliminarButton.setBackground(ContextCompat.getDrawable(context, R.color.colorRed));
            }else{
                int color = map( (int) difference_In_Minutes, 0, 10, 0, 255);
                int verde = 255 - color;
                int rojo = color;
                //holder.layoutBackground.setBackgroundColor(Color.rgb( rojo , verde , 0));
                holder.eliminarButton.setBackgroundColor(Color.rgb( rojo , verde , 0));
            }

        }
        catch (ParseException e) {
            e.printStackTrace();
        }


        holder.eliminarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data = holder.nombreTextView.getText().toString();
                webSocket.send(data);
            }
        });
    }

    private int map(int x, int in_min, int in_max, int out_min, int out_max) {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }

    public WebSocket getWebSocket() {
        return webSocket;
    }

    public void setWebSocket(WebSocket webSocket) {
        this.webSocket = webSocket;
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public void adicionarListaTupperIO(ArrayList<TupperIO> listaTupperIO) {
        dataset.clear();
        dataset.addAll(listaTupperIO);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView nombreTextView;
        private TextView dayTextView;
        private TextView durationDateTextView;
        private LinearLayout layoutBackground;
        private Button eliminarButton;

        public ViewHolder(View itemView) {
            super(itemView);
            layoutBackground = itemView.findViewById(R.id.layout_background);
            nombreTextView = itemView.findViewById(R.id.nombreTextView);
            dayTextView = itemView.findViewById(R.id.dayText);
            durationDateTextView = itemView.findViewById(R.id.durationDateText);
            eliminarButton = itemView.findViewById(R.id.button_eliminar);
        }
    }

}
