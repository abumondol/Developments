%clear all
%load('distances');
%load('pos_indices');

s = 1;
pos_count = length(pos_indices);
results = zeros(pos_count, 4);
fprintf('Pos count: %d\n', pos_count);
for i=1:pos_count        
    sid = pos_indices(i,1);
    index = pos_indices(i,2);
    dist = distances(sid).dist;
    
    d = dist(dist(:, 1)==index, [9, 16, 4, 2, 3]); %distance 3s left, dist 2s right, label, sub id, index
    d = [d(:,1)+d(:,2), d(:, 3:end)];
    
    d_pos = d(d(:, 2)>0, :);
    d_neg = d(d(:, 2)==0, :);
    mn_neg = min(d_neg(:, 1));    
    
    pos_covered = d_pos(d_pos(:,1)< mn_neg, :);
    if isempty(pos_covered)
        results(i,2) = mn_neg/2;
        %results(i,4) = mn_neg;
        continue;
    end
    
    mx_pos = max(pos_covered(:, 1));    
    results(i, 1) = length(pos_covered(:, 1));
    results(i, 2) = (mx_pos + mn_neg)/2;    
    results(i, 3) = mx_pos;
    results(i, 4) = mn_neg;    
end 

results = [results, pos_indices];
results = sortrows(results); 
results = flipud(results);
results = [results(:, end-1:end), results(:, 1:4)];
results_3_2 = results;
save('results_3_2', 'results_3_2');

return

selected_indices = [];
while ~isempty(results)
    sid = results(i,end-1);
    index = results(i,end);
    radius = results(i, 2);
    
    dist = distances(sid).dist;    
    d = dist(dist(:, 1)==index, [9, 16, 4, 2, 3]); %distance 3s left, dist 2s right, label, sub id, index
    d = [d(:,1)+d(:,2), d(:, 3:end)];
    d_pos = d(d(:, 2)>0, :);
    pos_covered = d_pos(d_pos(:,1)< mn_neg, end-1:end);    
end


