package com.example.tictactoe;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

public class MainActivity extends Activity {
    Button[] cells = new Button[9];
    String[] board = new String[9];
    TextView status;
    boolean gameOver = false;
    final int[][] wins = {{0,1,2},{3,4,5},{6,7,8},{0,3,6},{1,4,7},{2,5,8},{0,4,8},{2,4,6}};

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        resetBoard();
        buildUI();
    }

    void buildUI() {
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setGravity(Gravity.CENTER_HORIZONTAL);
        page.setPadding(28, 28, 28, 28);
        page.setBackgroundColor(Color.rgb(112,197,245));

        TextView title = new TextView(this);
        title.setText("Tic Tac Toe");
        title.setTextSize(38);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setTextColor(Color.BLACK);
        title.setGravity(Gravity.CENTER);
        page.addView(title, new LinearLayout.LayoutParams(-1, 80));

        status = new TextView(this);
        status.setText("Your turn — X");
        status.setTextSize(22);
        status.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        status.setGravity(Gravity.CENTER);
        page.addView(status, new LinearLayout.LayoutParams(-1, 65));

        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(3);
        grid.setRowCount(3);
        grid.setUseDefaultMargins(false);

        for (int i=0;i<9;i++) {
            final int index=i;
            Button btn=new Button(this);
            cells[i]=btn;
            btn.setTextSize(42);
            btn.setTextColor(Color.rgb(35,52,68));
            btn.setBackgroundColor(Color.rgb(238,244,255));
            btn.setOnClickListener(v -> playerMove(index));
            GridLayout.LayoutParams lp=new GridLayout.LayoutParams();
            lp.width=0; lp.height=0;
            lp.columnSpec=GridLayout.spec(i%3,1f);
            lp.rowSpec=GridLayout.spec(i/3,1f);
            lp.setMargins(5,5,5,5);
            grid.addView(btn,lp);
        }

        int sizeDp=(int)(Math.min(getResources().getDisplayMetrics().widthPixels*0.82f, 450));
        page.addView(grid,new LinearLayout.LayoutParams(sizeDp,sizeDp));

        Button reset=new Button(this);
        reset.setText("New Game");
        reset.setTextSize(19);
        reset.setOnClickListener(v -> { resetBoard(); render(); });
        LinearLayout.LayoutParams rp=new LinearLayout.LayoutParams(-2,70);
        rp.topMargin=22;
        page.addView(reset,rp);

        setContentView(page);
        render();
    }

    void resetBoard(){ for(int i=0;i<9;i++) board[i]=""; gameOver=false; }

    void render(){ if(cells==null)return; for(int i=0;i<9;i++) cells[i].setText(board[i]); }

    boolean won(String p){
        for(int[] w:wins) if(board[w[0]].equals(p)&&board[w[1]].equals(p)&&board[w[2]].equals(p)) return true;
        return false;
    }

    void playerMove(int i){
        if(gameOver || !board[i].equals("")) return;
        board[i]="X"; render();
        if(won("X")){ end("You win! 🎉"); return; }
        if(full()){ end("Draw! 🤝"); return; }
        status.setText("Bot is thinking...");
        new android.os.Handler().postDelayed(this::botMove,250);
    }

    void botMove(){
        if(gameOver)return;
        int move=-1;
        for(int i=0;i<9;i++) if(board[i].equals("")) {
            board[i]="O"; if(won("O")){move=i;board[i]="";break;} board[i]="";
        }
        if(move<0) for(int i=0;i<9;i++) if(board[i].equals("")) {
            board[i]="X"; if(won("X")){move=i;board[i]="";break;} board[i]="";
        }
        if(move<0 && board[4].equals("")) move=4;
        if(move<0) for(int i:new int[]{0,2,6,8}) if(board[i].equals("")){move=i;break;}
        if(move<0) for(int i=0;i<9;i++) if(board[i].equals("")){move=i;break;}
        board[move]="O"; render();
        if(won("O")){end("Bot wins! 🤖");return;}
        if(full()){end("Draw! 🤝");return;}
        status.setText("Your turn — X");
    }

    boolean full(){ for(String s:board) if(s.equals("")) return false; return true; }
    void end(String s){gameOver=true;status.setText(s);}
}
