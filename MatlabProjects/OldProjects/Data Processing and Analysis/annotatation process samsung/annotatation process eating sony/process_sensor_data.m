src_dir= 'data_src/';
files = dir(src_dir);
file_count = length(files)
time_offset = [90.5, 81.5, 59.9, 116.1, 212.4, 99.6];

for i = 3:file_count   
    file_path = strcat(src_dir, files(i).name);
    fprintf('Processing... %s; offset: %.2f\n', file_path, time_offset(i-2));
    
    d = csvread(char(file_path));    
    a = d(d(:,2)==1, [1,4:6]);
    t = a(1,1);
    a(:,1) = a(:,1) - a(1,1);
    a(:,1) = round(a(:,1)/1e9, 3);
    a(:,1) = a(:,1) + time_offset(i-2);
    
    
    dest_path = strcat('data_dest/', strrep(files(i).name, '.wada', '.csv'));
    
    fileID = fopen(dest_path,'w');
    fprintf(fileID,'time, acl_X, acl_Y, acl_Z\n');    
    fclose(fileID);    
    dlmwrite(dest_path, a, '-append');
end    


