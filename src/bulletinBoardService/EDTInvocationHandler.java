package bulletinBoardService;


import javax.swing.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class EDTInvocationHandler implements InvocationHandler {
    private UITasks ui;

    public EDTInvocationHandler(UITasks ui) {
        this.ui = ui;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (SwingUtilities.isEventDispatchThread()) {
            return method.invoke(ui, args);
        } else {
            final Object[] result = {null};
            Runnable task = () -> {
                try {
                    result[0] = method.invoke(ui, args);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            };
            SwingUtilities.invokeAndWait(task);
            return result[0];
        }
    }
}

