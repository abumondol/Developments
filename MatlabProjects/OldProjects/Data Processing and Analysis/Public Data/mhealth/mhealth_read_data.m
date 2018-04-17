src = 'C:/Users/mm5gg/Box Sync/Public Data/mhealth/MHEALTHDATASET/MHEALTHDATASET/';
mhealth_data = [];

for sub = 1:10    
    file_name = strcat(src,'mHealth_subject',num2str(sub),'.log');
    fprintf('File name: %s\n', file_name);
    d = dlmread(file_name);
    [r, c] = size(d);  
    fprintf('mhealth protocol: %d, %d, %d\n', sub, r, c);        
    
    mhealth_data(sub).session(1).labels = d(:, 24);
    
    mhealth_data(sub).session(1).position(1).name = 'right_wrist';
    mhealth_data(sub).session(1).position(1).accel = d(:, 15:17);
    mhealth_data(sub).session(1).position(1).gyro = d(:, 18:20);
    %mhealth_data(sub).session(1).position(1).mag = d(:, 14:16);
    
    mhealth_data(sub).session(1).position(2).name = 'left_ankle';
    mhealth_data(sub).session(1).position(2).accel = d(:, 6:8);
    mhealth_data(sub).session(1).position(2).gyro = d(:, 9:11);
    %mhealth_data(sub).session(1).position(2).mag = d(:, 31:33);
    
    %mhealth_data(sub).session(1).position(3).name = 'chest';
    %mhealth_data(sub).session(1).position(3).accel = d(:, 1:3);
    %mhealth_data(sub).session(1).position(3).gyro = d(:, 45:47);
    %mhealth_data(sub).session(1).position(3).mag = d(:, 48:50);   
    
end

save('mhealth_data', 'mhealth_data');