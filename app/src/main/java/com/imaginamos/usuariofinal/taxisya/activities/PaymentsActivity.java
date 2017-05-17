package com.imaginamos.usuariofinal.taxisya.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.imaginamos.usuariofinal.taxisya.R;
import com.imaginamos.usuariofinal.taxisya.adapter.RecyclerItemClickListener;
import com.imaginamos.usuariofinal.taxisya.io.ApiConstants;
import com.imaginamos.usuariofinal.taxisya.models.Card;
import com.imaginamos.usuariofinal.taxisya.models.Conf;
import com.loopj.android.http.TextHttpResponseHandler;
import com.paymentez.androidsdk.PaymentezSDKClient;
import com.paymentez.androidsdk.models.ListCardsResponseHandler;
import com.paymentez.androidsdk.models.PaymentezCard;
import com.paymentez.androidsdk.models.PaymentezResponseListCards;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class PaymentsActivity extends Activity {

    Conf conf;

    PaymentezSDKClient paymentezsdk;
    ArrayList<String> values = new ArrayList<>();
    ArrayList<Card> mCards = new ArrayList<Card>();

    EditText editTextUid;
    EditText editTextEmail;
    Button callApiAddWebView;
    Button callApiAddPci;
    Button callScanCard;

    ListView listView;
    RecyclerView mRecyclerView;

    ArrayAdapter<String> listAdapter;
    ArrayList<PaymentezCard> listCard;

    PaymentezCard paymentezCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setTheme(R.style.AppThemeCard);
        setContentView(R.layout.activity_payments);

        conf = new Conf(this);

        //Desarrollo true
        //Producción false
        paymentezsdk = new PaymentezSDKClient(this, ApiConstants.api_env, ApiConstants.app_code, ApiConstants.app_secret_key);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        callApiAddWebView = (Button) findViewById(R.id.btnAddCard);
        callApiAddWebView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String email = conf.getUser();
                String uuid = conf.getIdUser();
                String trimmedEmail = email.trim();
                String trimmedUuid = uuid.trim();

                Log.v("CHECK_CARD","email " + trimmedEmail);
                Log.v("CHECK_CARD","uuid " + trimmedUuid);

                if( (trimmedEmail == null) || trimmedEmail.equals("") || trimmedUuid.equals("")){
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(PaymentsActivity.this);
                    builder1.setMessage("Tiene que estar registrado");

                    builder1.setPositiveButton("Aceptar",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    finish();
                                }
                            });
                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }else {

                    Log.v("CHECK_CARD","email " + trimmedEmail);
                    Log.v("CHECK_CARD","uuid " + uuid);

                    paymentezsdk.addCardShowWebView(trimmedUuid, trimmedEmail, PaymentsActivity.this);

                }

            }
        });



    }

    public void getCards(){
        final ProgressDialog pd = new ProgressDialog(PaymentsActivity.this);
        pd.setMessage("");
        pd.show();

        values = new ArrayList<>();
        mCards = new ArrayList<>();

        String email = conf.getUser();
        String uuid = conf.getIdUser();
        String trimmedEmail = email.trim();
        String trimmedUuid = uuid.trim();

        Log.v("TCPY","getCards 1");

        paymentezsdk.listCards(trimmedUuid, new ListCardsResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, PaymentezResponseListCards response) {
                pd.dismiss();
                System.out.println("SUCCESS: " + response.isSuccess());
                System.out.println("CODE: " + response.getCode());

                listCard = response.getCards();
                //ArrayList<String> values = new ArrayList<>();
                Log.v("TCPY","getCards 2");

                for (int i = 0; i < response.getCards().size(); i++) {
                   Log.v("TCPY","getCards 3 " + response.getCards().size());

                    PaymentezCard card = response.getCards().get(i);
//                    values.add("nombre:" + card.getCardHolder() + "\ncard_reference:" + card.getCardReference());
//                    values.add(card.getCardReference());
                    values.add(card.getCardHolder() + "\n**** **** **** " + card.getTermination() + "\nVencimiento: " + card.getExpiryMonth() + "/" + card.getExpiryYear() );

                    boolean defCard = false;


                    try {

                        Log.v("TCPY","getCards 4 " + conf.getCardDefault());
                        if (conf.getCardDefault().length() > 1) {
                            Log.v("TCPY","getCards 5 a " + conf.getCardDefault());
                            Log.v("TCPY","getCards 6 a " + card.getCardReference());

                            if (conf.getCardDefault().equals(card.getCardReference())) {
                                defCard = true;
                            }
                        }
                    }
                    catch (Exception e) {

                    }

                    Card c = new Card(card.getCardHolder(), card.getTermination(), card.getCardReference(), card.getType(), card.getExpiryMonth(), card.getExpiryYear(),defCard );

//                    c.setReference(card.getCardReference());
//                    c.setName(card.getCardHolder() );
//                    c.setNumber(card.getTermination());
//                    c.setExpireMonth(card.getExpiryMonth());
//                    c.setExpireYear(card.getExpiryYear());
//                    c.setType(card.getType());

                    Log.v("TARJETA ", " c" + c.toString());

                    Log.v("TARJETA ", " c " + c.getName());
                    Log.v("TARJETA ", " c " + c.getNumber());
                    Log.v("TARJETA ", " c " + c.getType());
                    Log.v("TARJETA ", " c " + c.getExpireMonth() + "/" + c.getExpireYear());

                    mCards.add(c);

//                    System.out.println("CARD INFO");
//                    System.out.println(card.getCardHolder());
//                    System.out.println(card.getCardReference());
//                    System.out.println(card.getExpiryYear());
//                    System.out.println(card.getTermination());
//                    System.out.println(card.getExpiryMonth());
//                    System.out.println(card.getType());

                }

                setUpRecyclerView();
            }


        });
    }




    @Override
    protected void onResume() {
        super.onResume();

        getCards();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // else handle other activity results
    }

    private void setUpRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new TestAdapter());
        mRecyclerView.setHasFixedSize(true);
        setUpItemTouchHelper();
        setUpAnimationDecoratorHelper();
        mRecyclerView.addOnItemTouchListener(
            new RecyclerItemClickListener(PaymentsActivity.this, new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Log.v("TCPY","tc selected " + position + " " + mCards.get(position).getReference() + " " + mCards.get(position).getNumber());
                    conf.setCardDefault(mCards.get(position).getReference());
                    finish();
                }
            })
        );

    }

    private void setUpItemTouchHelper() {

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            // we want to cache these and not allocate anything repeatedly in the onChildDraw method
            Drawable background;
            Drawable xMark;
            int xMarkMargin;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(Color.RED);
                xMark = ContextCompat.getDrawable(PaymentsActivity.this, R.drawable.ic_clear_24dp);
                xMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                xMarkMargin = (int) PaymentsActivity.this.getResources().getDimension(R.dimen.ic_clear_margin);
                initiated = true;
            }

           // not important, we don't want drag & drop
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }


            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int position = viewHolder.getAdapterPosition();
                TestAdapter testAdapter = (TestAdapter)recyclerView.getAdapter();
                if (testAdapter.isUndoOn() && testAdapter.isPendingRemoval(position)) {
                    return 0;
                }
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int swipedPosition = viewHolder.getAdapterPosition();
                TestAdapter adapter = (TestAdapter)mRecyclerView.getAdapter();
                boolean undoOn = adapter.isUndoOn();
                if (undoOn) {
                    adapter.pendingRemoval(swipedPosition);
                } else {
                    adapter.remove(swipedPosition);
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;

                // not sure why, but this method get's called for viewholder that are already swiped away
                if (viewHolder.getAdapterPosition() == -1) {
                    // not interested in those
                    return;
                }

                if (!initiated) {
                    init();
                }

                // draw red background
                background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                background.draw(c);

                // draw x mark
                int itemHeight = itemView.getBottom() - itemView.getTop();
                int intrinsicWidth = xMark.getIntrinsicWidth();
                int intrinsicHeight = xMark.getIntrinsicWidth();

                int xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
                int xMarkRight = itemView.getRight() - xMarkMargin;
                int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight)/2;
                int xMarkBottom = xMarkTop + intrinsicHeight;
                xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);

                xMark.draw(c);

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

        };
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    private void setUpAnimationDecoratorHelper() {
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {

            // we want to cache this and not allocate anything repeatedly in the onDraw method
            Drawable background;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(Color.RED);
                initiated = true;
            }

            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

                if (!initiated) {
                    init();
                }

                // only if animation is in progress
                if (parent.getItemAnimator().isRunning()) {

                    // some items might be animating down and some items might be animating up to close the gap left by the removed item
                    // this is not exclusive, both movement can be happening at the same time
                    // to reproduce this leave just enough items so the first one and the last one would be just a little off screen
                    // then remove one from the middle

                    // find first child with translationY > 0
                    // and last one with translationY < 0
                    // we're after a rect that is not covered in recycler-view views at this point in time
                    View lastViewComingDown = null;
                    View firstViewComingUp = null;

                    // this is fixed
                    int left = 0;
                    int right = parent.getWidth();

                    // this we need to find out
                    int top = 0;
                    int bottom = 0;

                    // find relevant translating views
                    int childCount = parent.getLayoutManager().getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View child = parent.getLayoutManager().getChildAt(i);
                        if (child.getTranslationY() < 0) {
                            // view is coming down
                            lastViewComingDown = child;
                        } else if (child.getTranslationY() > 0) {
                            // view is coming up
                            if (firstViewComingUp == null) {
                                firstViewComingUp = child;
                            }
                        }
                    }

                    if (lastViewComingDown != null && firstViewComingUp != null) {
                        // views are coming down AND going up to fill the void
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                    } else if (lastViewComingDown != null) {
                        // views are going down to fill the void
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = lastViewComingDown.getBottom();
                    } else if (firstViewComingUp != null) {
                        // views are coming up to fill the void
                        top = firstViewComingUp.getTop();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                    }

                    background.setBounds(left, top, right, bottom);
                    background.draw(c);

                }
                super.onDraw(c, parent, state);
            }

        });
    }


    class TestAdapter extends RecyclerView.Adapter {

        private static final int PENDING_REMOVAL_TIMEOUT = 3000; // 3sec

        List<Card> items;
        List<Card> itemsPendingRemoval;
        int lastInsertedIndex; // so we can add some more items for testing purposes
        boolean undoOn; // is undo on, you can turn it on from the toolbar menu

        private Handler handler = new Handler(); // hanlder for running delayed runnables
        HashMap<String, Runnable> pendingRunnables = new HashMap<>(); // map of items to pending runnables, so we can cancel a removal if need be

        public TestAdapter() {
            items = new ArrayList<Card>();
            itemsPendingRemoval = new ArrayList<Card>();
            // let's generate some items
            lastInsertedIndex = 15;
            // this should give us a couple of screens worth
            for (int i=0; i < mCards.size(); i++) {
                //items.add("Item " + i);
                Log.v("CARD_INFO","mCard 1 " + mCards.get(i).getName());

                Card c = new Card(
                        mCards.get(i).getName(),
                        mCards.get(i).getNumber(),
                        mCards.get(i).getReference(),
                        mCards.get(i).getType(),
                        mCards.get(i).getExpireMonth(),
                        mCards.get(i).getExpireYear(),
                        mCards.get(i).isPrefered()
                        );
                Log.v("CARD_INFO","mCard 2 " + mCards.get(i).getNumber());

                items.add(c);

            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new TestViewHolder(parent);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            TestViewHolder viewHolder = (TestViewHolder)holder;
            //final String item = items.get(position);
            final Card item = items.get(position);

            if (itemsPendingRemoval.contains(item)) {
                // we need to show the "undo" state of the row
                viewHolder.itemView.setBackgroundColor(Color.RED);
                viewHolder.nameTextView.setVisibility(View.GONE);
                viewHolder.numberTextView.setVisibility(View.GONE);
                viewHolder.expirationTextView.setVisibility(View.GONE);

                viewHolder.undoButton.setVisibility(View.VISIBLE);
                viewHolder.undoButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.v("CARD1","onClick");
                        // user wants to undo the removal, let's cancel the pending task
                        Runnable pendingRemovalRunnable = pendingRunnables.get(item);
                        pendingRunnables.remove(item);

                        if (pendingRemovalRunnable != null) handler.removeCallbacks(pendingRemovalRunnable);
                        //itemsPendingRemoval.remove(item);
                        // this will rebind the row in "normal" state
                       // notifyItemChanged(items.indexOf(item));
                    }
                });
            } else {
                // we need to show the "normal" state
                viewHolder.itemView.setBackgroundColor(Color.WHITE);
                viewHolder.nameTextView.setVisibility(View.VISIBLE);
                viewHolder.numberTextView.setVisibility(View.VISIBLE);
                viewHolder.expirationTextView.setVisibility(View.VISIBLE);

                if (item.isPrefered()) {
                    viewHolder.defaultTextView.setVisibility(View.VISIBLE);
                }
                else {
                    viewHolder.defaultTextView.setVisibility(View.GONE);
                }

                viewHolder.nameTextView.setText(item.getName());
                viewHolder.numberTextView.setText(item.getNumber());
                viewHolder.expirationTextView.setText(item.getExpireMonth() + "/" + item.getExpireYear());

                viewHolder.undoButton.setVisibility(View.GONE);
                viewHolder.undoButton.setOnClickListener(null);
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        /**
         *  Utility method to add some rows for testing purposes. You can add rows from the toolbar menu.
         */
        public void addItems(int howMany){
            if (howMany > 0) {
                for (int i = lastInsertedIndex + 1; i <= lastInsertedIndex + howMany; i++) {
                    //items.add("Item " + i);

                    notifyItemInserted(items.size() - 1);
                }
                lastInsertedIndex = lastInsertedIndex + howMany;
            }
        }

        public void setUndoOn(boolean undoOn) {
            this.undoOn = undoOn;
        }

        public boolean isUndoOn() {
            return undoOn;
        }

        public void pendingRemoval(int position) {
            //final String item = items.get(position);
            final Card item = items.get(position);
            if (!itemsPendingRemoval.contains(item)) {
                Log.v("CARD1","pendingRemoval ");
                itemsPendingRemoval.add(item);
                // this will redraw row in "undo" state
                notifyItemChanged(position);
                // let's create, store and post a runnable to remove the item
                Runnable pendingRemovalRunnable = new Runnable() {
                    @Override
                    public void run() {
                        remove(items.indexOf(item));
                    }
                };
                handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
                pendingRunnables.put(item.getReference(), pendingRemovalRunnable);
            }
        }

        public void remove(int position) {
            //String item = items.get(position);
            Card item = items.get(position);
            if (itemsPendingRemoval.contains(item)) {
                itemsPendingRemoval.remove(item);
                Log.v("TC_DEL","remove 1");
            }
            if (items.contains(item)) {

                try {
                    // remove card prefered if marked to remove
                    if (conf.getCardDefault().length() > 1) {
                        if (conf.getCardDefault().equals(item.getReference())) {
                            conf.setCardDefault("0");
                        }
                    }
                }
                catch (Exception e) {

                }

                items.remove(position);
                Log.v("TC_DEL", "remove 2");
                notifyItemRemoved(position);

                // TODO: Refactor
                final ProgressDialog pd2 = new ProgressDialog(PaymentsActivity.this);
                pd2.setMessage("");
                pd2.show();

                String email = conf.getUser();
                String uuid = conf.getIdUser();
                String trimmedEmail = email.trim();
                String trimmedUuid = uuid.trim();

                paymentezsdk.deleteCard(trimmedUuid, item.getReference(), new TextHttpResponseHandler() {

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        pd2.dismiss();
                        System.out.println("Failure: " + responseString);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        pd2.dismiss();
                        Log.v("TC_DEL", "onSuccess");
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(PaymentsActivity.this);
                        builder1.setMessage(getString(R.string.card_add_msg1));
                        builder1.setCancelable(false);
                        builder1.setPositiveButton(getString(R.string.card_add_msg2),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Log.v("TC_DEL", "remove 3");
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alert11 = builder1.create();
                        alert11.show();
                        getCards();
                    }
                });
            }
        }

        public boolean isPendingRemoval(int position) {
            //String item = items.get(position);
            Card item = items.get(position);
            return itemsPendingRemoval.contains(item);
        }
    }

    /**
     * ViewHolder capable of presenting two states: "normal" and "undo" state.
     */
    static class TestViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;
        TextView numberTextView;
        TextView expirationTextView;
        TextView defaultTextView;

        Button undoButton;

        public TestViewHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_card, parent, false));
            nameTextView = (TextView) itemView.findViewById(R.id.title_holder);
            numberTextView = (TextView) itemView.findViewById(R.id.title_number);
            expirationTextView = (TextView) itemView.findViewById(R.id.title_expiration);
            defaultTextView = (TextView) itemView.findViewById(R.id.title_default);

            undoButton = (Button) itemView.findViewById(R.id.undo_button);
        }

    }

}
