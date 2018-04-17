src = 'C:/Users/mm5gg/Box Sync/Public Data/RealDisp/realistic_sensor_displacement/';
data = [];
for sub = 1:17    
    fprintf('Realdisp processing subject %d\n', sub);    
    file_name = strcat(src,'subject',num2str(sub),'_ideal.log')
    d = dlmread(file_name);
    size(d)
    
    if sum(sum(isnan(d))) > 0 
        fprintf('=================== Nan found ========================\n');
        return
    end
    
    t = d(:,1) + d(:,2)/1e6; % first column in second, second column in microsecond
    data(sub).t = t;    
    data(sub).activity = d(:, 120);
    
    accel = [];
    gyro = [];
    grav = [];
    for i = 3:13:107
        dx = d(:, i:i+12);
        ac = dx(:, 1:3);
        gy = dx(:, 4:6);
        %mg = dx(:, 7:9);
        qt = dx(:, 10:13);
        
        [Rx, Ry, Rz] = quaternion_to_rotation_matrix(qt);  
        accel = [accel, ac];
        gyro = [gyro, gy];
        grav = [grav, Rz];
    end
    
    data(sub).accel = accel;    
    data(sub).gyro = gyro;    
    data(sub).grav = grav;    
end

save('data', 'data');