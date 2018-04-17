src_dir= 'data_src/';
files = dir(src_dir);
file_count = length(files)
time_offset = 13.1;

for i = 4:file_count   
    file_path = strcat(src_dir, files(i).name);
    fprintf('Processing... %s; offset: %.2f\n', file_path, time_offset);
    
    d = csvread(char(file_path));
    start = 0;
    for s = 1:100
        if d(s,2)==1
            start = s;
            break
        end
    end
  
    d = d(start:end, :);
    %d(:,1) = d(:,1) - d(1,1);      
    %d(:,1) = round(d(:,1)/1e9, 3);
    %d(:,1) = d(:,1) + time_offset;
    
    
    acl = d(d(:,2)==1, [1,4:6]);
    grav = d(d(:,2)==9, [1,4:6]);
    
    for x=1:length(acl)
        if acl(x,1) ~=grav(x,1)
            display(x)
            break
        end
    end
    return
    
    len_acl = length(acl);
    len_grav = length(grav);
    for j=1:len_acl
        if acl(j,1) == grav(1,1)
            start = j;
            break
        end
    end
    display(start);
    
    grav = grav(:, 2:end);
    if start>1
        grav = [zeros(start-1, 3); grav];
    end
    
    
    d = [acl, grav];
    
    dest_path = strcat('data_dest/', strrep(files(i).name, '.wada', '-acl.csv'));    
    fileID = fopen(dest_path,'w');
    fprintf(fileID,'time, acl_X, acl_Y, acl_Z, grav_X, grav_Y, grav_Z\n');    
    fclose(fileID);    
    dlmwrite(dest_path, d, '-append');
end