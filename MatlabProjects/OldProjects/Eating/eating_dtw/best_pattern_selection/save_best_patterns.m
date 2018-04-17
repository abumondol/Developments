%load('results_3_2');
%load('../data');
%load('../segments');
res = results_3_2;
rescount = length(res);

fid = fopen('patterns','w');
patterns = zeros(rescount, 240);
for i = 1:rescount
    s = res(i,1);
    index = segments(s).min_points(res(i,2));
    d = data(s).accel(index-47:index+31, 2:4);
    d = [d(:,1);d(:,2);d(:,3)]';
    %d = [d, data(s).accel(index, 2), res(i, end), res(i,3)];
    d = [d, data(s).accel(index, 2), res(i, 4), res(i,3)]; %1-79: x, 80:158:y, 159:237:z, 238:min_x, 239: distance, covered_count
    patterns(i, :) = d;
end
csvwrite('patterns_v1.csv', patterns);
%save('patterns','patterns');