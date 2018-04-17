%load('raw_data_non_eat');
procdata_noneat = [];
subject_count = length(rawdata_noneat)

for sub=1:subject_count   
    fprintf('Subject: %d\n', sub);
    d = rawdata_noneat(sub).session(1).data;    

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

    accel(:,2:4) =  smooth(accel(:,2:4), 0.8);
    grav(:,2:4) =  smooth(grav(:,2:4), 0.8);
        
    
    procdata_noneat(sub).session(1).accel = accel;            
    procdata_noneat(sub).session(1).gyro = gyro;            
    procdata_noneat(sub).session(1).grav = grav;
    procdata_noneat(sub).session(1).linaccel = linaccel;
    procdata_noneat(sub).session(1).quat = quat;
    
end

save('procdata_noneat', 'procdata_noneat');
