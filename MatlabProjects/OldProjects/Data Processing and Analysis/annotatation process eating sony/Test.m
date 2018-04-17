src_dir= 'data_src/';
files = dir(src_dir);
file_count = length(files)
time_offset = [26.1, 17.1];

for i = 3:file_count   
    file_path = strcat(src_dir, files(i).name);
    fprintf('Processing... %s; offset: %.2f\n', file_path, time_offset(i-2));
    
    d = csvread(char(file_path));    
    d = d(d(:,2) == 9, [1,4:6]);
    d(:,1) = d(:,1) - d(1,1);
    d(:,1) = round(d(:,1)/1e9, 3);
    d(:,1) = d(:,1) + time_offset(i-2);
    
    dest_path = strcat('data_dest/', strrep(files(i).name, 'raw-data.wada', 'acl.csv'));
    
    fileID = fopen(dest_path,'w');
    fprintf(fileID,'time, acl_X, acl_Y\n');    
    fclose(fileID);    
    dlmwrite(dest_path, d, '-append');
end    


