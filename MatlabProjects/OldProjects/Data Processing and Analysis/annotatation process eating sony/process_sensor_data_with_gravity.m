src_dir= 'data_src/';
files = dir(src_dir);
file_count = length(files)
time_offset = [59.8];

for i = 3:file_count   
    file_path = strcat(src_dir, files(i).name);
    fprintf('Processing... %s; offset: %.2f\n', file_path, time_offset(i-2));
    
    data = csvread(char(file_path));    
    if data(1,2) == 0
        data = data(2:end, :);
    end
    
    a = data(data(:,2)==1, [1, 4:6]);    
    g = data(data(:,2)==9, [1, 4:6]);
    
    s = find(a(:,1) == g(1,1))
        
    a(:,1) = a(:,1) - a(1,1);
    a(:,1) = round(a(:,1)/1e9, 3);
    a(:,1) = a(:,1) + time_offset(i-2);
    g = [zeros(s-1, 3); g(:, 2:4)];
    d = [a, g]; 
            
    dest_path = strcat('data_dest/', strrep(files(i).name, '.wada', '.csv'));
    
    fileID = fopen(dest_path,'w');
    fprintf(fileID,'time, acl_X, acl_Y, acl_Z, grav_X, grav_Y, grav_Z\n'); 
    fclose(fileID);    
    dlmwrite(dest_path, d, '-append');
end    


