package cz.mgsoft.gai0006.dodgefish;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DrawFilter;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.StrictMode;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Size;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;

public class GameView extends View {

    HashMap<String, Bitmap> Textures;
    private boolean Stopped;
    private int Force = 10;
    private int Width;
    private int Height;
    private int MoveX;
    private int MoveY;
    public int Lerp = 0;
    public int PlyerLerpSide = 0;
    public int NextPlyerLerpSide = 0;

    public int FPS = 0;
    private int _fps = 0;
    public int UPS = 0;
    private int _ups = 0;
    private long LastUpdate = 0;
    private long LastUi = 0;
    public int RedrawDelay = 15;
    public int UpdateDelay = 30;

    public int Score = 0;
    private int TargetScore = 2;

    private Size Size = new Size(7,12);
    private Point Player = new Point(3,10);

    private ArrayList<int[]> Spawns;

    private int[] Map;

    private GameUpdateTask UpdateTimerTask;
    private Timer UpdateTimer;
    private Random Generator;

    public GameView(Context context) {
        super(context);
        init(context);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    protected void init(Context context) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Set textures
        Textures = new HashMap<>();
        AddTexture("sea", R.drawable.sea);
        AddTexture("player", R.drawable.fish_player);
        AddTexture("enemy", R.drawable.fish_enemy);
        AddTexture("hit", R.drawable.fish_enemy_hitted);

        // Build spawns
        Spawns = new ArrayList<>();
        /*ArrayList<Integer> fishes = new ArrayList<>();
        int[] data = new int[Size.getWidth()];
        for (int count = 0; count < (Size.getWidth() - 1); count++)
        {
            combinationBuilder(fishes, data, 0, fishes.size(), 0, Size.getWidth());
            Log.d("Game", "Combinations " + Integer.toString(Spawns.size()));
            fishes.add(1);
        }*/
        for(int t = 0; t < Size.getWidth();t++)
        {
            int[] data = new int[Size.getWidth()];
            for(int n = 0; n < Size.getWidth();n++)
                data[n] = 0;
            data[t] = 1;
            Spawns.add(data);
        }
        Log.d("Game", "Spawns " + Integer.toString(Spawns.size()));

        // Update
        UpdateTimer = new Timer();
        Generator = new Random(1);
        UpdateTimerTask = new GameUpdateTask(this);

        // Create map
        Map = new int[Size.getWidth()*Size.getHeight()];

        Log.d("Game", "Init Complete");
        Start();
    }

    public void AddTexture(String name, int resource)
    {
        Textures.put(name, BitmapFactory.decodeResource(getResources(), resource));
    }

    public void Start()
    {
        Log.d("Game", "Started");
        Stopped = false;
        UpdateTimer.schedule(UpdateTimerTask, RedrawDelay, RedrawDelay);
    }

    public void Stop()
    {
        Log.d("Game", "Stopped");
        Stopped = true;
        UpdateTimer.cancel();
    }

    protected void Update()
    {
        Score++;
        if(UpdateDelay > 4 && Score == TargetScore) {
            UpdateDelay -= 2;
            TargetScore *= 2;
        }

        // Move all enemies down
        for(int y = Size.getHeight()-1; y > 0; y--)
            for(int x = 0; x < Size.getWidth(); x++)
                Map[y*Size.getWidth()+x] = Map[(y-1)*Size.getWidth()+x];

        // Spawn new ones
        int i = Generator.nextInt(Spawns.size());
        for(int x = 0; x < Size.getWidth(); x++)
            Map[x] = Spawns.get(i)[x];

        // Update player
        switch (PlyerLerpSide)
        {
            case -1:
                // Left
                if(Player.x > 0)
                    Player.x--;
                break;
            case 1:
                // Right
                if(Player.x < (Size.getWidth()-1))
                    Player.x++;
                break;
        }
        PlyerLerpSide = NextPlyerLerpSide;
        NextPlyerLerpSide = 0;

        // Check hits
        if(Map[Player.y * Size.getWidth() + Player.x] == 1)
            Map[Player.y * Size.getWidth() + Player.x] = 2;

        invalidate();
    }

    protected void Redraw(){
        long time = System.currentTimeMillis()-LastUpdate;
        if(time >= 1000) {
            FPS = _fps;
            _fps = 0;
            LastUpdate = System.currentTimeMillis();
        }
        _fps++;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        long time = System.currentTimeMillis()-LastUi;
        if(time >= 1000) {
            UPS = _ups;
            _ups = 0;
            LastUi = System.currentTimeMillis();
        }
        _ups++;

        canvas.drawBitmap(Textures.get("sea"), null, new Rect(0,0, Width, Height), null);
        canvas.drawBitmap(Textures.get("player"), null, GetFish(Player.x, Player.y, true), null);
        for(int y = 0; y < Size.getHeight(); y++)
            for(int x = 0; x < Size.getWidth(); x++) {
                String texture = null;
                switch (Map[y * Size.getWidth() + x])
                {
                    case 1:
                        texture = "enemy";
                        break;
                    case 2:
                        texture = "hit";
                        break;
                }
                if(texture != null)
                    canvas.drawBitmap(Textures.get(texture), null, GetFish(x, y, false), null);
            }

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(20);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("FPS: "+Integer.toString(FPS)+ "   UPS: "+Integer.toString(UPS)+"   Player["+Integer.toString(Player.x)+","+Integer.toString(Player.y) + "]   Score: "+Integer.toString(Score)+"/"+Integer.toString(TargetScore)+"   Speed: "+Integer.toString((32-UpdateDelay)/2), 0, (0+paint.getTextSize()), paint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Width = w;
        Height = h;
        MoveX = w/Size.getWidth();
        MoveY = h/Size.getHeight();
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                int x = (int)event.getX();
                if(x < MoveX*3) {
                    // Left
                    if(Player.x > 1 || (Player.x == 1 && PlyerLerpSide > -1))
                        NextPlyerLerpSide = -1;
                }
                else if(x > (Width-MoveX*3))
                {
                    // Right
                    if(Player.x < (Size.getWidth()-2) || (Player.x < (Size.getWidth()-1) && PlyerLerpSide < 1))
                        NextPlyerLerpSide = 1;
                }
                else
                    NextPlyerLerpSide = 0;
                break;
        }

        return super.onTouchEvent(event);
    }

    /* arr[]  ---> Input Array
        data[] ---> Temporary array to store current combination
        start & end ---> Staring and Ending indexes in arr[]
        index  ---> Current index in data[]
        r ---> Size of a combination to be printed */
    protected void combinationBuilder(ArrayList<Integer> arr, int data[], int start, int end, int index, int size)
    {
        // Current combination is ready to be printed, print it
        if (index == size)
        {
            Spawns.add(data);
        }

        // replace index with all possible elements. The condition
        // "end-i+1 >= r-index" makes sure that including one element
        // at index will make a combination with remaining elements
        // at remaining positions
        for (int i=start; i<=end && end-i+1 >= size-index; i++)
        {
            data[index] = arr.get(i);
            combinationBuilder(arr, data, i+1, end, index+1, size);
        }
    }

    protected Rect GetFish(int x, int y, boolean player)
    {
        return new Rect(GetX(x, player), GetY(y, player), GetX(x+1, player), GetY(y+1, player));
    }

    protected int GetX(int x, boolean player)
    {
        return (int)(MoveX*x+(player?(MoveX*(Lerp*PlyerLerpSide)/(float)UpdateDelay):0));
    }

    protected int GetY(int y, boolean player)
    {
        return (int)(MoveY*y+(player?0:(MoveY*Lerp/(float)UpdateDelay)));
    }
}

