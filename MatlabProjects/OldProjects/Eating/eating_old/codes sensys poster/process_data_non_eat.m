%load('raw_data_non_eat');
data_non_eat = [];
subject_count = length(raw_data_non_eat)

for sub=1:5    
    fprintf('Subject: %d\n', sub);
    d = raw_data_non_eat(sub).session(1).data;    

    if d(1,2) == 0
        d = d(2:end, :);
    end

    d(:,1) = d(:,1)-d(1,1);
    d(:,1) = d(:,1)/1e9;

    accel = d(d(:,2)==1, [1, 4:6]);
    gyro = d(d(:,2)==4, [1, 4:6]);
    grav = d(d(:,2)==9, [1, 4:6]);
    quat = d(d(:,2)==11, [1, 4:6]);

    s = find(accel(:,1) == grav(1,1));
    linaccel = [grav(:,1), accel(s:end, 2:4) - grav(:, 2:4)]; 

    data_non_eat(sub).session(1).accel = accel;            
    data_non_eat(sub).session(1).gyro = gyro;            
    data_non_eat(sub).session(1).grav = grav;
    data_non_eat(sub).session(1).linaccel = linaccel;
    data_non_eat(sub).session(1).quat = quat;
    
end

save('data_non_eat', 'data_non_eat');
