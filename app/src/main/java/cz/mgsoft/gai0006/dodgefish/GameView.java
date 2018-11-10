package cz.mgsoft.gai0006.dodgefish;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Size;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class GameView extends View {

    HashMap<String, Bitmap> Textures;
    private boolean Stopped;
    private int Force = 10;
    private int Width;
    private int Height;
    private int MoveX;
    private int MoveY;

    private int UpdateDelay = 50;

    private Size Size = new Size(7,12);
    private Point Player = new Point(3,10);

    private ArrayList<int[]> Spawns;

    private int[] Map;

    private Timer UpdateTimer;

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

        // Build spawns
        Spawns = new ArrayList<>();
        ArrayList<Integer> fishes = new ArrayList<>();
        int[] data = new int[Size.getWidth()];
        for (int count = 0; count < (Size.getWidth() - 1); count++)
        {
            combinationBuilder(fishes, data, 0, fishes.size(), 0, Size.getWidth());
            Log.d("Game", "Combinations" + Integer.toString(Spawns.size()));
            fishes.add(1);
        }

        // Update
        UpdateTimer = new Timer();

        // Create map
        Map = new int[Size.getWidth()*Size.getHeight()];
        for(int i = 0; i < Map.length; i++)
            Map[i] = 0;

        Log.d("Game", "Init Complete");
    }

    public void AddTexture(String name, int resource)
    {
        Textures.put(name, BitmapFactory.decodeResource(getResources(), resource));
    }

    public void Start()
    {
        Log.d("Game", "Started");
        Stopped = false;
        UpdateTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Update();
            }
        }, UpdateDelay);
    }

    public void Stop()
    {
        Log.d("Game", "Stopped");
        Stopped = true;
        UpdateTimer.cancel();
    }

    protected void Update()
    {
        Log.d("Game", "Update");

        // Move all enemies down
        for(int y = Size.getHeight()-1; y > 0; y--)
            for(int x = 0; x < Size.getWidth(); x++)
                Map[y*Size.getWidth()+x] = Map[(y-1)*Size.getWidth()+x];

        // ToDo Spawn new one
        
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d("Game", "Draw "+Integer.toString(Width)+","+Integer.toString(Height));
        canvas.drawBitmap(Textures.get("sea"), null, new Rect(0,0, Width, Height), null);
        canvas.drawBitmap(Textures.get("player"), null, GetFish(Player), null);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Width = w;
        Height = h;
        MoveX = w/Size.getWidth();
        MoveY = h/Size.getHeight();
        super.onSizeChanged(w, h, oldw, oldh);
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

    protected Rect GetFish(Point position)
    {
        return new Rect(GetX(position.x),GetY(position.y),GetX(position.x+1), GetY(position.y+1));
    }

    protected int GetX(int x)
    {
        return MoveX*x;
    }

    protected int GetY(int y)
    {
        return MoveY*y;
    }
}

