package com.danito.p_agendaavanzada;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.danito.p_agendaavanzada.pojo.ContactoContainer;

public class ContactoViewModel extends ViewModel {
    private MutableLiveData<ContactoContainer> liveData;

    public LiveData<ContactoContainer> getData(){
        if (liveData != null) {
            setData(liveData.getValue());
        } else {
            liveData = new MutableLiveData<>();
        }
        return liveData;
    }

    public void setData(ContactoContainer contacto) {
        if (liveData == null) {
            liveData = new MutableLiveData<>();
        } else {
            liveData.setValue(contacto);
        }
    }
}
