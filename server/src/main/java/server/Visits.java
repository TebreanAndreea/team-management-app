package server;

import org.springframework.stereotype.Service;


@Service
public class Visits {
    private int num;

    public Visits ()
    {
        num = 0;
    }

    public void increase()
    {
        num++;
    }

    public int ret()
    {
        return num;
    }
}
