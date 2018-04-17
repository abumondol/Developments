features = [];
sub_count = length(hw);
for i=1:sub_count    
    episode_count = length(hw(i).data);

    %r = ceil(rand(3)*episode_count);
    for j= 1:episode_count
        fprintf('Accumulating features: subject: %d, episode: %d\n',i, j);
        
        f = hw(i).data(j).features;         
        f_count = size(f,1);
        sub = ones(f_count,1)*i;
        epis = ones(f_count,1)*j;
        features = [features; f epis sub];        
    end
end

save('features','features');

