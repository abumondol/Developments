close all; clc;

g = hw(1).data(1).alg(:,8:10);
g = sqrt(sum(g.*g,2));
g = median(g);
factor = 10;

sub_count = length(hw);
for i=1:1%sub_count
    episode_count = length(hw(i).data);
    
    for j= 1:episode_count        
        t = strrep(hw(i).data(j).file_name,'_', ', ');         
        grav = hw(i).data(j).alg(:,8:10);        
        grav = grav/g;   
        graph_grid_single(grav, factor, t, M);          
    end
end