src_dir= 'data/';
files = dir(src_dir);
file_count = length(files)
time_offset = 13.1;

data = [];
for i = 3:file_count   
    file_path = strcat(src_dir, files(i).name);
    fprintf('Processing... %s; offset: %.2f\n', file_path, time_offset);
    
    d = csvread(char(file_path));
    
    accel = d(d(:, 2)==1, :);
    gyro = d(d(:, 2)==4, :);
    grav = d(d(:, 2)==9, :);
    linacc = d(d(:, 2)==10, :);
    
    sen =accel;
    t = (sen(end, 1) - sen(1,1))/1e9;
    accel_rate = length(sen)/t;
    
    sen =grav;
    t = (sen(end, 1) - sen(1,1))/1e9;
    grav_rate = length(sen)/t;
    
    sen =linacc;
    t = (sen(end, 1) - sen(1,1))/1e9;
    linacc_rate = length(sen)/t;
    
    sen =gyro;
    t = (sen(end, 1) - sen(1,1))/1e9;
    gyro_rate = length(sen)/t;
    
    data(i-2).accel = accel;
    data(i-2).gyro = gyro;
    data(i-2).grav = grav;
    data(i-2).linacc = linacc;
        
    
end