package huaiye.com.vvs.models.auth;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class KickOutUIObserver {
    KickOutHandler kickOutHandler;
    public KickOutUIObserver(){

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onKickout(final UIKickout data) {
        if (this.kickOutHandler != null){
            this.kickOutHandler.onKickOut();
        }
    }

    public void start(KickOutHandler kickOutHandler){
        this.kickOutHandler = kickOutHandler;
        if (!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
    }

    public void stop(){
        this.kickOutHandler = null;
        if (EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
    }

    public static class UIKickout{

    }
}
