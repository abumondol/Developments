function result = process_data_false()
load('sub_data_false');
total_false_sub = length(sub_data_false);
movavg_points = 5;
result = true;

for i=1:total_false_sub
    fprintf('Processing false signatures, subject %d\n', i);
    
    false_sign_count = length(sub_data_false(i).sign);
    for j =1:false_sign_count     
    
        if sub_data_false(i).sign(j).processed > 0
            fprintf('%d Already Processed: %s \n', i, sub_data_false(i).sign(j).file_name);
            continue;
        else
            fprintf('%d Processing: %s \n', j, sub_data_false(i).sign(j).file_name);
        end
       
        data = sub_data_false(i).sign(j).raw_data;
        acc = data(data(:,2)== 1, 3:5);       
        acc = tsmovavg(acc, 'e', movavg_points, 1);    
        acc(1:movavg_points-1, :) = 0;
        if sum(isnan(acc)) > 0
            fprintf('There is NaN in accelerometer data');
            result = false;
            return;
        end
    
        acc_mag = sqrt(sum(acc.*acc,2));
        acc = [acc, acc_mag];    
        [a, b] = mark_signature(acc);  
        %a=30;
        %b=100;    
        acc = acc(a:b,:);
        sub_data_false(i).sign(j).acc_nz = acc;
        acc = zscore(acc);
        sub_data_false(i).sign(j).acc = acc;
        sub_data_false(i).sign(j).acc_mp =[a, b];
        
        % calculate gyro mark points 
        acc_time = data(data(:,2)== 1, 1);
        st_acc = acc_time(a,1);
        et_acc = acc_time(b,1);
        sub_data_false(i).sign(j).duration = et_acc - st_acc;
    
        gyro_time = data(data(:,2)== 4, 1);
        gyro = data(data(:,2)== 4, 3:5);
        gyro = tsmovavg(gyro,'e', movavg_points, 1);
        gyro(1:movavg_points-1, :) = 0;
        if sum(isnan(gyro)) > 0
            fprintf('There is NaN in gyro data');
            exit;
        end    
    
        gyro = gyro( gyro_time(:,1) >= st_acc & gyro_time(:,1) <= et_acc, :);   
        gyro_mag = sqrt(sum(gyro.*gyro,2));
        gyro = [gyro, gyro_mag];
        sub_data_false(i).sign(j).gyro_nz = gyro;
        gyro = zscore(gyro);    
        sub_data_false(i).sign(j).gyro = gyro;
        sub_data_false(i).sign(j).gyro_mp =[];
    
    
    sub_data_false(i).sign(j).acc_ppx = find_peak_points(acc(:,1));
    sub_data_false(i).sign(j).acc_ppy = find_peak_points(acc(:,2));
    sub_data_false(i).sign(j).acc_ppz = find_peak_points(acc(:,3));
    sub_data_false(i).sign(j).acc_ppm = find_peak_points(acc(:,4));
    
    sub_data_false(i).sign(j).gyro_ppx = find_peak_points(gyro(:,1));
    sub_data_false(i).sign(j).gyro_ppy = find_peak_points(gyro(:,2));
    sub_data_false(i).sign(j).gyro_ppz = find_peak_points(gyro(:,3));
    sub_data_false(i).sign(j).gyro_ppm = find_peak_points(gyro(:,4));
    
    sub_data_false(i).sign(j).processed = 1;
    
end %end of for

save('sub_data_false','sub_data_false');
fprintf('Processing false signatures done\n\n');

end
