package com.paradicedev.dailyst;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

public class AppManualDialog extends DialogFragment{

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Dailyst user manual");
        builder.setMessage("\n- Swipe horizontally to switch between weekdays buttons and general tasks list" +
                "\n\n- Using week days buttons you can check tasks for each day of incoming week" +
                "\n\n- Yellow floating button allows to add new task to your list" +
                "\n\n- Press task on list to show \"Done\" button" +
                "\n\n- Press and hold task on list to show more details button" +
                "\n\n\nEnjoy!" +
                "\n");
        builder.setPositiveButton(R.string.dialog_close_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        return builder.create();
    }
}
