load('sub_data');
sub_count = length(sub_data);
%load('sub_mac');


type = 2;
for i=1:sub_count
    sign_count = sub_data(i).sign_count;
    fprintf('Checking subject: %d, sign_count:%d\n', i, sign_count);
    
    for j = 1:sign_count-1
        for k = j+1:sign_count            
            dist = sub_data(i).grid_type(type).dist_grid(j,k).dist;
            status = sub_data(i).grid_type(type).dist_grid(j,k).status;
            if length(dist)==0 || status ~= 1
                fprintf('Error: %d %d\n', j,k);
                return;
            end
            
            dist = sub_data(i).grid_type(type).dist_grid(k,j).dist;
            status = sub_data(i).grid_type(type).dist_grid(k,j).status;
            if length(dist)==0 || status ~= 1
                fprintf('Error: %d %d\n', j,k);
                return;
            end
            
%             sub_data(i).grid_type(type).dist_grid(j,k).dist = dist;
%             sub_data(i).grid_type(type).dist_grid(k,j).dist = dist;            
%             sub_data(i).grid_type(type).dist_grid(j,k).status = 1;
%             sub_data(i).grid_type(type).dist_grid(k,j).status = 1;
        end
    end
    %save('sub_data','sub_data');
end




