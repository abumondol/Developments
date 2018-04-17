function res = process_sensor_data_grav_acl_aligned(d)
    start = 0;
    for s = 1:100
        if d(s,2)==1
            start = s;
            break
        end
    end
  
    d = d(start:end, :);
    d(:,1) = d(:,1) - d(1,1);
    
    duration = d(end, 1)/1e9;
    fprintf('Data duration: %.0f:%.0f\n', duration/60, mod(duration, 60));
    
    acl = d(d(:,2)==1, [1,4:6]);
    gyro = d(d(:,2)==4, [1,4:6]);
    grav = d(d(:,2)==9, [1,4:6]);
    quat = d(d(:,2)==11, [1,4:7]);
    
    [Rx, Ry, Rz] = quaternion_to_rotation_matrix([quat(:,5), quat(:,2:4)]);
    quat = [quat(:,1), Rz, grav(:, 2:4)];
    
    quat_count = length(quat);
    grav_count = length(grav);
    acl_count = length(acl);
    gyro_count = length(gyro);
    fprintf('Lengths (acl, gyro, gravm quat): %d, %d, %d, %d\n', acl_count, gyro_count, grav_count, quat_count);
    
    %%%%% Aligning accel %%%%%%%%%%%
    new_acl = zeros(quat_count-1, 3);    
    j = 1;
    ix = 1;
    while ix<quat_count && j<acl_count
        t1 = acl(j,1);
        t2 = acl(j+1,1);
        tx = quat(ix,1);
        
       if tx >= t1 && tx < t2
            factor = (tx-t1)/(t2-t1);
            new_acl(ix, :) = (1-factor)*acl(j,2:4) + factor*acl(j+1,2:4);
            ix = ix +1;
       end
        
       tx = quat(ix,1);
       if tx >= t2
        j = j + 1;
       end        
    end
   
 %%%%% Aligning gyro %%%%%%%%%%%
    new_gyro = zeros(quat_count-1, 3);    
    j = 1;
    ix=1;
    while ix<quat_count && j<gyro_count
        t1 = gyro(j,1);
        t2 = gyro(j+1,1);
        tx = quat(ix,1);
        
        if tx < t1
            printf('xxxxxxxxxxxx Error in TX xxxxxxxxxxxxxxxx');
            return
        end
        
       if tx >= t1 && tx < t2
            factor = (tx-t1)/(t2-t1);
            new_gyro(ix, :) = (1-factor)*gyro(j,2:4) + factor*gyro(j+1,2:4);
            ix = ix +1;
       end
        
        tx = quat(ix,1);
        if tx >= t2
            j = j + 1;
        end
        
    end
    
    
    d = [quat(1:end-1, :), new_acl, new_gyro];
    d(:,1) = round(d(:,1)/1e9, 3);
    res = d;
    
end