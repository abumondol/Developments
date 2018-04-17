src_dir= 'data_src/';
files = dir(src_dir);
file_count = length(files)
time_offset = zeros(100,1);

for i = 3:file_count   
    file_path = strcat(src_dir, files(i).name);
    fprintf('Processing... %s; offset: %.2f\n', file_path, time_offset(i-2));
    
    d = csvread(char(file_path));
    
    d(:,1) = round(d(:,1)/1e6);
    m = min(d(:,1));    
    d(:,1) = d(:,1)-m;    
    %d = d(d(:,2) == 1 | d(:,2) == 4 | d(:,2) == 11 |d(:,2) == 9 | d(:,2) == 2, :);
    
    accel = d(d(:,2)==1, :);
    mag = d(d(:,2)==2, :);
    ornt = d(d(:,2)==3, :);
    gyro = d(d(:,2)==4, :);
    grav = d(d(:,2)==9, :);
    quat = d(d(:,2)==11, :);
    
    accel_rate = 1000*length(accel)/(accel(end,1)-accel(1,1))
    grav_rate = 1000*length(accel)/(grav(end,1)-grav(1,1))
    gyro_rate = 1000*length(accel)/(gyro(end,1)-gyro(1,1))
    quat_rate = 1000*length(accel)/(quat(end,1)-quat(1,1))
    
    return
    flag = 0;
    j = 1;
    len = size(d,1);
    while j < len-4
        if d(j,2) == 1 && d(j+1,2) == 4 && d(j+2,2) == 11 && d(j+3,2) == 9 && d(j+4,2) == 2
            flag = 1;
            break
        end
        j = j + 1;
    end
    display(j);
    d = d(j:end, :);
    t = d(d(:,2)== 1, 1);
    accel = d(d(:,2)== 1, 4:6);
    gyro = d(d(:,2) == 4, 4:6);
    grav = d(d(:,2) == 9, 4:6);
    magnet = d(d(:,2) == 2, 4:6);
    quat = d(d(:,2) == 11, [7, 4:6]);
    size(accel)
    size(grav)
    laccel = accel - grav;
    [Rx, Ry, Rz] = quaternion_to_rotation_matrix(quat);
    z_theta_Y = radtodeg(atan2(Rx(:,3), Ry(:,3)));
    z_theta_Z = 180 - radtodeg(acos(Rz(:,3)));
    
    d = [t, accel, gyro, grav, laccel, magnet, Rx, Ry, Rz, z_theta_Y, z_theta_Z];
    
    samsung = d;
    save('samsung', 'samsung');
    
    dest_path = strcat('data_dest/', strrep(files(i).name, '.wada', '.csv'));
    
    fileID = fopen(dest_path,'w');
    fprintf(fileID,'time, accel_X, accel_Y, accel_Z, gyro_X, gyro_Y, gyro_Z, grav_X, grav_y, grav_z, laccel_X, laccel_Y, laccel_Z, mag_X, mag_Y, mag_Z, Rx_x, Rx_y, Rx_z, Ry_x, Ry_y, Ry_z, Rz_x, Rz_y, Rz_z, z_theta_Y, z_theta_Z\n');    
    fclose(fileID);    
    dlmwrite(dest_path, d, '-append');
end    


