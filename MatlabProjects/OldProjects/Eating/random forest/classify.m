sub_count = length(data);
window_length = 80;
step_size = 16;
minx_th = -2;
var_th = 0.15;

%res = cell(sub_count, 1);
res = [];
tr = train_data;
for sub = 1:sub_count
    fprintf('Subject %d\n', sub);
    tr = train_data(train_data(:, end)~=sub, :);
    trainX = tr(:, 1:end-2);
    trainY = tr(:, end-1);
    model = TreeBagger(30, trainX, trainY);
    
    testX = get_test_data(data(sub).accel(:, 2:4), window_length, step_size, minx_th, var_th);
    [Yfit, scores] = predict(model, testX(:, 1:end-1));
    %size(testX)
    %size(Yfit)
    %size(scores)
    %size(testX(:, end))
    res{sub}.ind = testX(:, end);
    res{sub}.Yfit = Yfit;
    res{sub}.scores = scores;
end

save('res', 'res');