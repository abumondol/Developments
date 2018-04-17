figure
plot(res(:, 1), res(:,end-2), 'b-', 'LineWidth', 2);
hold on
plot(res(:, 1), res(:,end-1), 'b--', 'LineWidth', 2);
plot(res(:, 1), res(:,end), 'b-.', 'LineWidth', 2);
ylim([0,100]);
xlabel('\alpha_{xr}');
ylabel('Percentage(%)');
legend('FRR', 'PR (HDS)', 'PR(HEDS)');
