package com.mobmonkey.mobmonkeyandroid.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobmonkey.mobmonkeyandroid.R;
import com.mobmonkey.mobmonkeyandroid.utils.MMFragment;

public class Fragment_1 extends MMFragment{

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		return inflater.inflate(R.layout.fragment_1, null);
		
	}

	@Override
	public void onFragmentBackPressed() {
		// TODO Auto-generated method stub
		
	}
}
