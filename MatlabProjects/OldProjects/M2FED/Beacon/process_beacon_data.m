src_dir= 'data_2/';
files = dir(src_dir);
file_count = length(files)
time_offset = [3, 0, 15];
beacondata = [];

for i = 3:file_count   
    file_path = strcat(src_dir, files(i).name);
    fprintf('Processing... %s; offset: %.2f\n', file_path, time_offset(i-2));
    
    d = csvread(char(file_path));            
    d(:,1) = d(:,1) - time_offset(i-2);    
    count = size(d, 1);
    watch_id = zeros(count,1) + (i-2);
    d = [watch_id, d];
    beacondata = [beacondata; d];
end    

beacondata(:,2) = beacondata(:,2) - min(beacondata(:,2));
beacondata(:,2) = beacondata(:,2)/1000;

save('beacondata', 'beacondata');


