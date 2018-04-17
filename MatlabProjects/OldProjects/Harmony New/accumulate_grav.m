all_grav = [];
sub_count = length(hw);
for i=1:sub_count    
    episode_count = length(hw(i).data);    
    for j= 1:episode_count
        fprintf('Accumulating gravity: subject: %d, episode: %d\n',i, j);        
        f = hw(i).data(j).alg(:, 8:10);         
        f_count = size(f,1);
        subject = ones(f_count,1)*i;
        episode = ones(f_count,1)*j;
        all_grav = [all_grav; f episode subject];        
    end
end

g = all_grav(:, 1:3);
g = sqrt(sum(g.*g,2));
min(g)
mean(g)
median(g)
g = max(g)

all_grav(:,1:3) = all_grav(:, 1:3)/g;
save('all_grav','all_grav','g');