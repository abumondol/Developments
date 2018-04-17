load('sub_data');
sub_count = length(sub_data);

type = 2;
for i=2:sub_count
    sign_count = sub_data(i).sign_count;
    fprintf('Processing subject: %d, sign_count:%d\n', i, sign_count);
    
    for j = 1:sign_count-1
        for k = j+1:sign_count
            fprintf('Processing type %d, sub %d: %d, %d\n', type, i, j, k);
            dist = distance( sub_data(i).sign(j), sub_data(i).sign(k), type );
            sub_data(i).grid_type(type).dist_grid(j,k).dist = dist;
            sub_data(i).grid_type(type).dist_grid(k,j).dist = dist;            
            sub_data(i).grid_type(type).dist_grid(j,k).status = 1;
            sub_data(i).grid_type(type).dist_grid(k,j).status = 1;
        end
    end
    save('sub_data','sub_data');
end


