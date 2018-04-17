dur_eat = 0;
dur_non_eat = 0;
for sub =1:5
    for sess = 1:2
        accel = data(sub).session(sess).accel;
        dur_eat = dur_eat + accel(end,1) - accel(1,1);
        
        if sess == 1
            accel = data_non_eat(sub).session(sess).accel;
            dur_non_eat = dur_non_eat + accel(end,1) - accel(1,1);
        end
        
    end
end

dur_eat 
dur_non_eat

dur_eat = dur_eat/3600
dur_non_eat = dur_non_eat /3600

    