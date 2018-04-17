g = all_grav;

test_neg = g(g(:, end) == 1, 1:3);
test_pos = g(g(:, end) == 3, 1:3);
train = g(g(:, end) == 2, 1:3);

figure;
hist(train(:,1));
title('Train - X');

figure;
hist(train(:,2));
title('Train - Y');

figure;
hist(train(:,3));
title('Train - Z');

return

g = hw(1).data(1).alg(:,8:10);
g = sqrt(sum(g.*g,2));
g = median(g);

all_g = [];

sub_count = length(hw);
for i=2:sub_count
    episode_count = length(hw(i).data);    
    for j= 1:episode_count
        
        grav = hw(i).data(j).alg(:,8:10);
        grav =grav/g;        
        all_g = [all_g; grav];        
    end    
end

[coeff,score,latent] = pca(all_g);

save('coeff_latent','coeff','latent');