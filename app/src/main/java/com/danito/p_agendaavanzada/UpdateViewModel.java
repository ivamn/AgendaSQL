package com.danito.p_agendaavanzada;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.danito.p_agendaavanzada.pojo.ContactoContainer;
import com.danito.p_agendaavanzada.pojo.UpdateContainer;

public class UpdateViewModel extends ViewModel {
    private MutableLiveData<UpdateContainer> liveData;

    public LiveData<UpdateContainer> getData(){
        if (liveData != null) {
            setData(liveData.getValue());
        } else {
            liveData = new MutableLiveData<>();
        }
        return liveData;
    }

    public void setData(UpdateContainer updateContainer) {
        if (liveData == null) {
            liveData = new MutableLiveData<>();
        } else {
            liveData.setValue(updateContainer);
        }
    }
}
