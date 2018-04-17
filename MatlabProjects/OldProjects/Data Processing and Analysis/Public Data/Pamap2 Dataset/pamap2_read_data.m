src1 = 'C:/Users/mm5gg/Box Sync/Public Data/pamap2/PAMAP2_Dataset/PAMAP2_Dataset/Protocol/';
src2 = 'C:/Users/mm5gg/Box Sync/Public Data/pamap2/PAMAP2_Dataset/PAMAP2_Dataset/Optional/';
pamap2_data = [];

for sub = 1:9    
    file_name = strcat(src1,'subject',num2str(100+sub),'.dat');
    fprintf('File name: %s\n', file_name);
    d = dlmread(file_name);
    [r, c] = size(d);  
    fprintf('Pamap2 protocol: %d, %d, %d\n', sub, r, c);
    
    pamap2_data(sub).session(1).t = d(:,1);
    pamap2_data(sub).session(1).labels = d(:,2);        
    
    pamap2_data(sub).session(1).position(1).name = 'hand';
    pamap2_data(sub).session(1).position(1).accel = d(:, 5:7);
    pamap2_data(sub).session(1).position(1).gyro = d(:, 11:13);
    %pamap2_data(sub).session(1).position(1).mag = d(:, 14:16);
    
    pamap2_data(sub).session(1).position(2).name = 'chest';
    pamap2_data(sub).session(1).position(2).accel = d(:, 22:24);
    pamap2_data(sub).session(1).position(2).gyro = d(:, 28:30);
    %pamap2_data(sub).session(1).position(2).mag = d(:, 31:33);
    
    pamap2_data(sub).session(1).position(3).name = 'ankle';
    pamap2_data(sub).session(1).position(3).accel = d(:, 39:41);
    pamap2_data(sub).session(1).position(3).gyro = d(:, 45:47);
    %pamap2_data(sub).session(1).position(3).mag = d(:, 48:50);   
    
end

for sub = [1, 5, 6, 8, 9]   
    file_name = strcat(src2,'subject',num2str(100+sub),'.dat');
    fprintf('File name: %s\n', file_name);
    d = dlmread(file_name);
    [r, c] = size(d);  
    fprintf('Pamap2 optional: %d, %d, %d\n', sub, r, c);
    
    pamap2_data(sub).session(2).t = d(:,1);
    pamap2_data(sub).session(2).labels = d(:,2);        
    
    pamap2_data(sub).session(2).position(1).name = 'hand';
    pamap2_data(sub).session(2).position(1).accel = d(:, 5:7);
    pamap2_data(sub).session(2).position(1).gyro = d(:, 11:13);
    pamap2_data(sub).session(2).position(1).mag = d(:, 14:16);
    
    pamap2_data(sub).session(2).position(2).name = 'chest';
    pamap2_data(sub).session(2).position(2).accel = d(:, 22:24);
    pamap2_data(sub).session(2).position(2).gyro = d(:, 28:30);
    pamap2_data(sub).session(2).position(2).mag = d(:, 31:33);
    
    pamap2_data(sub).session(2).position(3).name = 'ankle';
    pamap2_data(sub).session(2).position(3).accel = d(:, 39:41);
    pamap2_data(sub).session(2).position(3).gyro = d(:, 45:47);
    pamap2_data(sub).session(2).position(3).mag = d(:, 48:50);   
    
end

save('pamap2_data', 'pamap2_data');