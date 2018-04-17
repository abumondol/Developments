function result = process_data()
load('signatures');
len = length(signatures);
movavg_points = 5;
result = true;

start_i = 1;
end_i = len;

fprintf('Processing signatures... Total signatures: %d\n', len);
for i=start_i:end_i
    if signatures(i).processed > 0
        fprintf('%d Already Processed: %s \n', i, signatures(i).file_name);
        continue;
    else
        fprintf('%d Processing: %s \n', i, signatures(i).file_name);
    end
       
    data = signatures(i).raw_data;
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
    signatures(i).acc_nz = acc;
    acc = zscore(acc);
    signatures(i).acc = acc;
    signatures(i).acc_mp =[a, b];
        
    % calculate gyro mark points 
    acc_time = data(data(:,2)== 1, 1);
    st_acc = acc_time(a,1);
    et_acc = acc_time(b,1);
    signatures(i).duration = et_acc - st_acc;
    
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
    signatures(i).gyro_nz = gyro;
    gyro = zscore(gyro);    
    signatures(i).gyro = gyro;
    signatures(i).gyro_mp =[];
    
    
    signatures(i).acc_ppx = find_peak_points(acc(:,1));
    signatures(i).acc_ppy = find_peak_points(acc(:,2));
    signatures(i).acc_ppz = find_peak_points(acc(:,3));
    signatures(i).acc_ppm = find_peak_points(acc(:,4));
    
    signatures(i).gyro_ppx = find_peak_points(gyro(:,1));
    signatures(i).gyro_ppy = find_peak_points(gyro(:,2));
    signatures(i).gyro_ppz = find_peak_points(gyro(:,3));
    signatures(i).gyro_ppm = find_peak_points(gyro(:,4));
    
    signatures(i).processed = 1;
    
end %end of for

save('signatures','signatures');
fprintf('Processing signatures done\n\n');

end
